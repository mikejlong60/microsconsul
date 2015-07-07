import com.orbitz.consul.Consul

import scala.concurrent.duration._


object Runner  extends  App{

  val consul = Consul.newClient()
  val agent = consul.agentClient()
  val service2 = BoundOrbitzService(agent,"myservice","2",8888, TTLCheck(60.seconds))
  val service1= BoundOrbitzService(agent,"myservice","1",8888, TTLCheck(60.seconds))

  service1.register()
  service2.register()
  print("Hit 1 to exit")
  while(io.StdIn.readInt() != 1){


  }
  service1.deregister()
  service2.deregister()
  print("exiting")



}
