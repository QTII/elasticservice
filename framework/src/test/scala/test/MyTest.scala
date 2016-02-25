package test

import java.io.File

object MyModule {
    def abs(n: Int): Int =
        if (n < 0) -n
        else n

    def factorial(n: Int): Int = {
        def go(n: Int, acc: Int): Int =
            if (n <= 0) acc
            else go(n - 1, n * acc)

        go(n, 1)
    }

    private def formatAbs(x: Int) = {
        val msg = "The absolute value of %d is %d"
        msg.format(x, abs(x))
    }

    private def formatFactorial(n: Int) = {
        val msg = "The factorial of %d is %d"
        msg.format(n, factorial(n))
    }

    def formatResult(name: String, n: Int, f: Int => Int) = {
        val msg = "The %s of %d is %d"
        msg.format(name, n, f(n))
    }

    def main(args: Array[String]): Unit = {
        println(formatAbs(-42))
        println(formatFactorial(7))
    }

    def findFirst[A](as: Array[A], p: A => Boolean): Int = {
        @annotation.tailrec
        def loop(n: Int): Int =
            if (n >= as.length) -1
            else if (p(as(n))) n
            else loop(n + 1)

        loop(0)
    }

    def isSorted[A](as: Array[A], gt: (A,A) => Boolean): Boolean = {
        @annotation.tailrec
        def go(n: Int): Boolean =
            if (n >= as.length-1) true
            else if (!gt(as(n), as(n+1))) false
            else go(n+1)

        go(0)
    }
    
    def partial1[A, B, C](a: A, f: (A, B) => C): B => C =
    	b => f(a, b)
    	
    def curry[A, B, C](f: (A, B) => C): A => (B => C) =
    	a => b => f(a, b)
    	
    def uncurry[A, B, C](f: A => B => C): (A, B) => C =
    	(a, b) => f(a)(b)
    	
    def compose[A, B, C](f: B => C, g: A => B): A => C =
    	a => f(g(a))
    	
    val af = (a: String) => 1
    val bf = (b: Int) => "1"
    def test(): String => String = af andThen bf //bf compose af
}

import State._

case class State[S,+A](run: S => (A,S)) {
    def flatMap[B](f: A => State[S, B]): State[S, B] = State(s => {
        val (a, s1) = run(s)
        f(a).run(s1)
      })

    def map[B](f: A => B): State[S, B] =
        flatMap(a => unit(f(a)))

    def map2[B,C](rb: State[S,B])(f: (A, B) => C): State[S,C] =
        flatMap(a => rb.map(b => f(a,b)))
}

object State {
    def unit[S, A](a: A): State[S, A] =
        State(s => (a, s))

    def sequenceViaFoldRight[S,A](sas: List[State[S, A]]): State[S, List[A]] =
        sas.foldRight(unit[S, List[A]](List()))((f, acc) => f.map2(acc)(_ :: _))

    def sequence[S, A](sas: List[State[S, A]]): State[S, List[A]] = {
        def go(s: S, actions: List[State[S,A]], acc: List[A]): (List[A],S) =
            actions match {
                case Nil => (acc.reverse,s)
                case h :: t => h.run(s) match { case (a,s2) => go(s2, t, a :: acc) }
            }
        State((s: S) => go(s,sas,List()))
    }

    def get[S]: State[S, S] = State(s => (s, s))

    def set[S](s: S): State[S, Unit] = State(_ => ((), s))

    def modify[S](f: S => S): State[S, Unit] = for {
        s <- get
        _ <- set(f(s))
    } yield ()
}

sealed trait Input
case object Coin extends Input
case object Turn extends Input

case class Machine(locked: Boolean, candies: Int, coins: Int)

object Candy {
  def update = (i: Input) => (s: Machine) =>
    (i, s) match {
      case (_, Machine(_, 0, _)) => s
      case (Coin, Machine(false, _, _)) => s
      case (Turn, Machine(true, _, _)) => s
      case (Coin, Machine(true, candy, coin)) =>
        Machine(false, candy, coin + 1)
      case (Turn, Machine(false, candy, coin)) =>
        Machine(true, candy - 1, coin)
    }

  def simulateMachine(inputs: List[Input]): State[Machine, (Int, Int)] = for {
    _ <- sequence(inputs map (modify[Machine] _ compose update))
    s <- get
  } yield (s.coins, s.candies)
}