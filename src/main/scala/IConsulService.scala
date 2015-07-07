import java.net.URL
import java.nio.file.Path
import java.util.{Timer, TimerTask}

import com.orbitz.consul.AgentClient
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.FiniteDuration

trait ConsulCheck

case class TTLCheck(duration: FiniteDuration) extends ConsulCheck

case class URLCheck(uri: URL, interval: FiniteDuration) extends ConsulCheck

case class ScriptCheck(uri: Path, interval: FiniteDuration) extends ConsulCheck

case class MultiCheck(checks: Set[ConsulCheck]) extends ConsulCheck

case class ServiceDescription(name: String, id: String, port: Int, check: ConsulCheck,tags:List[String])

trait IConsulService {
  val description: ServiceDescription

  def register()

  def deregister()

  def pass()
}


trait OrbitzConsulService extends IConsulService {

  val agent: AgentClient
  val description: ServiceDescription
  lazy val checkId =  description.id

  def register(): Unit = registerCheck(description.check)


  private def registerCheck(check: ConsulCheck): Unit = {
    check match {

      case TTLCheck(d) => agent.register(description.port, d.toSeconds, description.name, description.id,description.tags:_*)
      case URLCheck(u, d) => agent.register(description.port, u, d.toSeconds, description.name, description.id)
      case MultiCheck(set) => set.foreach(c => registerCheck(c))
    }
  }


  def deregister() = agent.deregister(description.id)

  def pass() = agent.pass(checkId)
}

object BoundOrbitzService {

  def apply(agentClient: AgentClient, name: String, id: String, port: Int, check: ConsulCheck,tags:List[String]=Nil) = {

    new BoundOrbitzService(agentClient, ServiceDescription(name, id, port, check,List(name+"-"+id)++tags))
  }

}

class BoundOrbitzService(val agent: AgentClient, val description: ServiceDescription)
  extends OrbitzConsulService
  with LazyLogging {

  private case class PassTimerTask() extends TimerTask {
    def run = {
      logger.info("checking in\n")
      pass()
    }
  }

  private var timer: Option[Timer] = None

  override def register() = {
    super.register()
    pass()
    timer match {
      case None =>
        timer = Some(new Timer())
        description.check match {

          case TTLCheck(d) =>
            val interval = d.toMillis / 4
            timer.map(_.schedule(new PassTimerTask(),0, interval))
        }
      case _ => timer

    }
  }

  override def deregister() = {
    timer.map(_.cancel())
    super.deregister()
  }

}


