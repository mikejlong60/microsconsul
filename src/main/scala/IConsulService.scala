import java.net.URL
import java.nio.file.Path
import java.util.{Timer, TimerTask}

import com.orbitz.consul.AgentClient

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration

trait ConsulCheck

case class TTLCheck(duration: FiniteDuration) extends ConsulCheck

case class URLCheck(uri: URL, interval: FiniteDuration) extends ConsulCheck

case class ScriptCheck(uri: Path, interval: FiniteDuration) extends ConsulCheck

case class MultiCheck(checks: Set[ConsulCheck]) extends ConsulCheck

case class ServiceDescription(name: String, id: String, port: Int, check: ConsulCheck)

trait IConsulService {
  val description: ServiceDescription

  def register()

  def deregister()

  def pass()
}

trait OrbitzConsulService extends IConsulService {

  val agent: AgentClient
  val description: ServiceDescription

  def register(): Unit = registerCheck(description.check)


  private def registerCheck(check: ConsulCheck): Unit = {
    check match {
      case TTLCheck(d) => agent.register(description.port, d.toSeconds, description.name, description.id)
      case URLCheck(u, d) => agent.register(description.port, u, d.toSeconds, description.name, description.id)
      case MultiCheck(set) => set.foreach(c => registerCheck(c))
    }
  }


  def deregister() = agent.deregister(description.id)

  def pass() = agent.deregister(description.id)
}

class BoundOrbitzService(val agent: AgentClient, val description: ServiceDescription)
  extends OrbitzConsulService {

  private case class PassTimerTask(service: IConsulService) extends TimerTask {
    def run = service.pass()
  }

  private var timer: Option[Timer] = None


  override def register() = {
    timer match {
      case None =>
        timer = Some(new Timer())
        description.check match { // handle multicheck
        case TTLCheck(d) => timer.map(_.schedule(new PassTimerTask(this), d.toSeconds))
      }
      case _ =>timer

    }

    super.register()
  }

  override def deregister() = {
    timer.map(_.cancel())
    super.deregister()
  }

}
