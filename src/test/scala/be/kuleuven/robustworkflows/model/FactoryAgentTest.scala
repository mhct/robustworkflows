package be.kuleuven.robustworkflows.model

import org.scalatest.FunSuite
import scala.collection.mutable.Stack
import akka.testkit.TestActorRef
import org.mockito.Mockito
import com.mongodb.DB
import java.util.List
import akka.actor.ActorRef
import akka.testkit.TestActorRef
import akka.actor.ActorSystem

class F_actoryAgentTest extends FunSuite {

	test("do a simple test and see what happens") {
		val stack = new Stack[Int];
		stack.push(1)
		stack.push(290)
		
		val oldSize = stack.size
		val result = stack.pop()
		
		assert(result === 290)
		assert(stack.size === oldSize - 1)
	}
	
	test("FactoryAgent behaviour") {
	  val db = Mockito.mock(classOf[DB])
	  val neighbors = Mockito.mock(classOf[List[ActorRef]])
	  implicit val system = ActorSystem.create("bla")
	  
	  val ref = TestActorRef(new FactoryAgent(db, neighbors))
	  
	}
	
}

object FactoryAgentTest {
	type Environment = String => Int
	
	def eval(t: Tree, env: Environment): Int = t match {
		case Sum (l, r) => eval(l, env) + eval(r, env)
		case Var (n)	=> env(n)
		case Const (v)	=> v
	}
	
	def main(args: Array[String]) {
		val exp: Tree = Sum(Const(6),Const(3))
		val another: Const = Const(5)
		val porra: Var = new Var("fff")
		
		val env: Environment = {case "x" => 5 case "y" => 9}
		println("Expression: " + exp)
		println("Evaluation: " + eval(exp, env))
		
	}
}

abstract class Tree
case class Sum(l: Tree, r: Tree) extends Tree
case class Var(n: String) extends Tree
case class Const(v: Int) extends Tree
  
