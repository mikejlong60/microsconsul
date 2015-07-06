import com.orbitz.consul.Consul

import scala.concurrent.duration._


object Runner  extends  App{

  val consul = Consul.newClient()
  val agent = consul.agentClient()
  val service = BoundOrbitzService(agent,"myservice","2",8888, TTLCheck(10.seconds))

  service.register()
  print("Hit 1 to exist")
  while(io.StdIn.readInt() != 1){


  }
  service.deregister()
  print("exiting")



}
