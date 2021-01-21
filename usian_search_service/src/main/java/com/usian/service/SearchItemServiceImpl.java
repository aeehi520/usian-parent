package com.usian.service;
import com.github.pagehelper.PageHelper;
import com.usian.mapper.SearchItemMapper;
import com.usian.pojo.SearchItem;
import com.usian.utils.JsonUtils;
import org.aspectj.lang.annotation.Before;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchItemServiceImpl implements SearchItemService{

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${ES_INDEX_NAME}")
    private String ES_INDEX_NAME;

    @Value("${ES_TYPE_NAME}")
    private String ES_TYPE_NAME;
    @Override
    public boolean importAll() {
        try {
            if(!isExistsIndex()){
                createIndex();
            }
            int page=1;
            while (true){
                /**分页每次导入一千条*/
                PageHelper.startPage(page,1000);
                //1、查询mysql中的商品信息
                List<SearchItem> esDocumentList = searchItemMapper.getItemList();
                if(esDocumentList==null || esDocumentList.size()==0){
                    break;
                }
                BulkRequest bulkRequest = new BulkRequest();
                for (int i = 0; i < esDocumentList.size(); i++) {
                    SearchItem searchItem =  esDocumentList.get(i);
                    //2、把商品信息添加到es中
                    bulkRequest.add(new IndexRequest(ES_INDEX_NAME, ES_TYPE_NAME).
                            source(JsonUtils.objectToJson(searchItem), XContentType.JSON));
                }
                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                page++;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<SearchItem> selectByQ(String q, Long page, Integer pageSize) {
        SearchRequest searchRequest = new SearchRequest("usian");
        searchRequest.types("item");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] fieldNames = new String[]{"item_title","item_desc","item_sell_point","item_category_name"};
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(q,fieldNames));
        searchSourceBuilder.from((int) ((page-1)*pageSize));
        searchSourceBuilder.size(pageSize);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("item_title");
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHit[] hits = response.getHits().getHits();
        List<SearchItem> searchItemList = new ArrayList<>();
        for (int i=0; i<hits.length; i++){
            SearchHit hit = hits[i];
            SearchItem searchItem = JsonUtils.jsonToPojo(hit.getSourceAsString(),SearchItem.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null && highlightFields.size()>0) {
                searchItem.setItem_title(highlightFields.get("item_title").getFragments()[0].toString());
            }
            searchItemList.add(searchItem);
        }
        return searchItemList;
    }

    @Override
    public int insertDocument(String itemId) {
        //根据商品id查询商品信息
        SearchItem searchItem = searchItemMapper.getItemById(Long.valueOf(itemId));

        //将商品信息添加到索引库
        IndexRequest indexRequest = new IndexRequest(ES_INDEX_NAME,ES_TYPE_NAME);
        indexRequest.source(JsonUtils.objectToJson(searchItem), XContentType.JSON);
        IndexResponse response = null;
        try {
            response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getShardInfo().getFailed();
    }

    @Override
    public int deleteDocument(String itemId) {
        SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME);
        searchRequest.types(ES_TYPE_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("id", itemId));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHit[] hits = searchResponse.getHits().getHits();
        String id = null;
        if (hits.length>0){
            id = hits[0].getId();
        }
        DeleteRequest deleteRequest = new DeleteRequest(ES_INDEX_NAME, ES_TYPE_NAME, id);
        DeleteResponse response = null;
        try {
            response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getShardInfo().getFailed();
    }

    @Override
    public int updateDocument(String itemId) {
        int a = deleteDocument(itemId);
        int b = insertDocument(itemId);
        return a+b;
    }

    /**
     * 索引库是否存在
     * @return
     * @throws IOException
     */
    private boolean isExistsIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(ES_INDEX_NAME);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 创建索引库
     * @return
     * @throws IOException
     */
    private boolean createIndex() throws IOException {
        //创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(ES_INDEX_NAME);
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards",2)
                .put("number_of_replicas",1));
        createIndexRequest.mapping(ES_TYPE_NAME, "{\n" +
                "  \"_source\": {\n" +
                "    \"excludes\": [\n" +
                "      \"item_desc\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"item_title\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_sell_point\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_price\": {\n" +
                "      \"type\": \"float\"\n" +
                "    },\n" +
                "    \"item_image\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"index\": false\n" +
                "    },\n" +
                "    \"item_category_name\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_desc\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);
        //创建索引操作客户端
        IndicesClient indices = restHighLevelClient.indices();

        //创建响应对象
        CreateIndexResponse createIndexResponse =
                indices.create(createIndexRequest,RequestOptions.DEFAULT);
        //得到响应结果
        return createIndexResponse.isAcknowledged();
    }
}