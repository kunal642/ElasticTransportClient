
import java.io.{File, PrintWriter}

import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.deletebyquery.{DeleteByQueryAction, DeleteByQueryResponse}
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.query.QueryBuilders

import scala.io.Source
import scala.util.Random

/**
  * Created by kunal on 18/3/16.
  */
trait ClientServiceApi {
    val client:Client = EsClient.client

  def getCount = {
    client.prepareSearch("library").execute().actionGet().getHits.totalHits()
  }

  def searchAll ={
    client.prepareSearch("library").execute().actionGet()

  }

  def search(query:String,value:String) ={
    client.prepareSearch("library").setTypes("books").setQuery(QueryBuilders.termQuery(s"$query",s"$value")).execute().actionGet()
  }

  def update(id:String,field:String,value:Int):UpdateResponse ={
    client.prepareUpdate("library","books",id).setDoc(field, value).execute().actionGet()
  }

  def postFilter(must:String,mustNot:String) = {
    val postfilter = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("price",3800))
    client.prepareSearch("library").setTypes("books").setQuery(QueryBuilders.boolQuery()
      .must(QueryBuilders.nestedQuery("author",QueryBuilders.matchQuery("author.first","kunal")))).setPostFilter(postfilter).execute().actionGet()
  }

  /*def aggregationQuery = {
    client.prepareSearch("prod_glass_v3_sep2015").setQuery(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),QueryBuilders.queryFilter(QueryBuilders
      .andQuery(QueryBuilders.queryFilter(QueryBuilders
        .andQuery(QueryBuilders.queryFilter(QueryBuilders.termQuery("brand_id","170"))
          ,QueryBuilders.rangeQuery("content_published_yyyymmdd").from(null).to("20160322").includeLower(true).includeUpper(true)
          ,QueryBuilders.termsQuery("gender",List("male","female","unknown"))
          ,QueryBuilders.termsQuery("valence_direction",List("positive","negative","neutral"))
          ,QueryBuilders.termsQuery("primary_emotion",List("unclassified","anger","confusion","crave","disappointment","excitement","frustration","gratitude","happiness"))
          ,QueryBuilders.termQuery("content_media_type","my_data")
          ,QueryBuilders.termsQuery("primary_emotion_confidence",List("high","medium","low"))
          ,QueryBuilders.rangeQuery("sample").from(null).to("100").includeLower(true).includeUpper(true)
          ,QueryBuilders.rangeQuery("standard_ispersonal").from(0).to(null).includeLower(true).includeUpper(true)))
        ,QueryBuilders.nestedQuery("topic_json",QueryBuilders.queryFilter(QueryBuilders.termsQuery("topic_json.name","unclassified")))
        ,QueryBuilders.nestedQuery("persona_json",QueryBuilders.queryFilter(QueryBuilders.termsQuery("persona_json.name","perf1")))
        ,QueryBuilders.nestedQuery("purchase_path_json",QueryBuilders.queryFilter(QueryBuilders.termsQuery("purchase_path_json.name","unclassified")))
        ,QueryBuilders.nestedQuery("filter_json",QueryBuilders.queryFilter(QueryBuilders.termsQuery("filter_json.name",List("promo","test"))))))))
      .setPostFilter(QueryBuilders.boolQuery().mustNot(QueryBuilders.nestedQuery("filter_json",QueryBuilders.queryFilter(QueryBuilders.termsQuery("filter_json.name","perf2")))))
          .addAggregation(AggregationBuilders.filter("recent_sales").filter(QueryBuilders.queryFilter(QueryBuilders.rangeQuery("sold").from("now-1M")))
            .subAggregation(AggregationBuilders.terms("groupby").field("valence_direction").size(1000).shardSize(1000).order(Terms.Order.term(true))))
  }*/

  def add(author:String,title:String,price:Int,id:String):IndexResponse = {
    val jsonString = s"""
  {
    "title": "$title",
    "price": $price,
    "author":{
      "first": "${author.split(" ")(0)}",
      "last": "${author.split(" ")(1)}"
    },
    "id": "$id"
  }
    """

    client.prepareIndex("library", "books", id).setSource(jsonString)
      .get()
  }

  def addBig = {
    val bulkRequest:BulkRequestBuilder = client.prepareBulk()
    for (id <- 6 to 10000) {
      bulkRequest.add(client.prepareIndex("library", "books").setSource(
        s"""
  {
    "title": "title-$id",
    "price": ${Random.nextInt(2000) + 1000},
    "author":{
      "first": "first-$id",
      "last": "last-$id"
    },
    "id": "$id"
  }
    """))

    }
     bulkRequest.execute().actionGet()
  }

  def addFromJsonFile ={
    val fileData = Source.fromFile("/home/kunal/myjson.json").getLines().toList
    val bulkRequest:BulkRequestBuilder = client.prepareBulk()
    fileData.map{
      json => bulkRequest.add(client.prepareIndex("library","books").setSource(json))
    }
    bulkRequest.execute().actionGet()
  }

  def writeToFile ={
    val data = client.prepareSearch("library").setTypes("books").execute().actionGet()
    val writer = new PrintWriter(new File("/home/kunal/esjson.json"))
    writer.write(data.toString)
    writer.flush()
    print(data)
  }

  def delete(id:Int) = {
    val delQuery =  QueryBuilders.boolQuery.must(QueryBuilders.termsQuery("_id",id))
    val delResponse: DeleteByQueryResponse = DeleteByQueryAction.INSTANCE
      .newRequestBuilder(client)
      .setIndices("library")
      .setQuery(delQuery).execute().actionGet()
    delResponse
  }




}

class ClientService extends ClientServiceApi




