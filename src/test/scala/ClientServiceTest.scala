import org.scalatest.FunSuite

/**
  * Created by kunal on 18/3/16.
  */
class ClientServiceTest extends FunSuite with ClientServiceApi{

  val clientService = new ClientService

  test("get count"){
    val result = clientService.getCount
    assert(result === 6)
  }

  test("get all records"){
    val result =  clientService.searchAll
    assert(result.getHits.totalHits === 6)
  }

  test("update"){
      val response = clientService.update("1","price",10)
      assert(response.getVersion === 6)
  }

  test("index from json file"){
    val response = clientService.addFromJsonFile
    assert(response.hasFailures === false)
  }

  test("search"){
    val result = clientService.search("price","2800")
    assert(result.getHits.totalHits === 1)
  }

  test("delete"){
    val result = clientService.delete(2)
    assert(result.getTotalDeleted === 1)
  }

  test("add"){
    val result = clientService.add("kk tt","kk",10000,"2")
    assert(Integer.parseInt(result.getId) === 2)
  }

  test("post filter"){
    val result = clientService.postFilter("","")
    assert(result.getHits.getMaxScore === 1.287682F)
  }

  test("multiple add"){
    val res = clientService.addBig
    assert(true)
  }

  test("write to external file"){
    val result = clientService.writeToFile
    assert(true)
  }

}
