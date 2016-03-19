
import org.elasticsearch.action.deletebyquery.{DeleteByQueryAction, DeleteByQueryResponse}
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.index.query.QueryBuilders

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

  def add(name:String,title:String,price:Int,id:String):IndexResponse = {
    client.prepareIndex("library", "books", id)
      .setSource(XContentFactory.jsonBuilder()
        .startObject()
        .field("name", name)
        .field("title",title)
        .field("price", price)
        .endObject()
      )
      .get()
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


