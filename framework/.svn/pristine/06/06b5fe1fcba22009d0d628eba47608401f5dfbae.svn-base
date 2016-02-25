package test

import scala.util.Try
import scala.util.Success

object ScalazTest {
  def test1(a: List[Int]): Try[List[Int]] =
  	traverse(a)(x => test2(x))
  	
  def test2(a: Int): Try[Int] = {
  	println(a)
  	Try(1 / a)
  }
  
  def traverse[A,B](a: List[A])(f: A => Try[B]): Try[List[B]] =
    a match {
        case Nil => Success(Nil)
        case h :: t => f(h) flatMap (hh => traverse(t)(f) map (hh :: _))
    }
  
  def main(args: Array[String]) {
  	val a = test1(List(1,0,2))
  	println(a)
  }
}