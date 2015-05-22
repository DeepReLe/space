package bbdl.space
import bbdl.space._
import breeze.linalg.{DenseVector, DenseMatrix}
import org.scalatest.{Matchers, FlatSpec}
import bbdl.space.SampleDataFunctions.PreExpansion.{A1,A2,A3,b1,b2,b3,deltas1,deltas2,deltas3}

/**
*
Created by B
Time: 16:18 PM, 5/21/2015;
Project: space
*/

class KSystemConstraintsSpec() extends FlatSpec with Matchers{
  behavior of "PadWithZeroMatrices"
  it should "add the correct padding of zeros when it's the first (leftmost) System" in {
    assert(AbsDiff.Matricies(KSystemConstraints.PadWithZeroMatrices(A1, 0, 3), SampleDataFunctions.PostExpansion.paddedA1) < 1E-14)
  }
  it should "add the correct padding of zeros when it's the middle (non edge) System" in {
    val StackedA = DenseMatrix.vertcat(A2,-A2)
    val exp = KSystemConstraints.PadWithZeroMatrices(StackedA, 1, 3)
    val theoretical = SampleDataFunctions.PostExpansion.paddedA2
    assert(AbsDiff.Matricies(theoretical, exp)  < 1E-14)
  }
  it should "add the correct padding of zeros when it's the last (rightmost) System" in {
    assert(AbsDiff.Matricies(KSystemConstraints.PadWithZeroMatrices(A3, 2, 3), SampleDataFunctions.PostExpansion.paddedA3)  < 1E-14)
  }

  behavior of "PadWithZeroMatrices"
  val Vec = DenseVector(1.0,0.0,-1.0)
  it should "add the correct padding of zeros when it's the first (leftmost) System" in {
    assert(AbsDiff.Vectors(KSystemConstraints.PadWithZeroVector(Vec, 0, 6), DenseVector(1.0,0.0,-1.0,0.0,0.0,0.0)) < 1E-14)
  }
  it should "add the correct padding of zeros when it's the middle (non edge) System" in {
    val theo = DenseVector(0.0,1.0,0.0,-1.0,0.0,0.0)
    val exp = KSystemConstraints.PadWithZeroVector(Vec, 1, 6)
    assert(AbsDiff.Vectors(theo,exp) < 1E-14)
  }
  it should "add the correct padding of zeros when it's the last (rightmost) System" in {
    assert(AbsDiff.Vectors(KSystemConstraints.PadWithZeroVector(Vec, 3, 6), DenseVector(0.0,0.0,0.0,1.0,0.0,-1.0)) < 1E-14)
  }
  "DeltaConstraintMatrix" should "construct a matrix for an A=2,2 K=3 small example" in {
    val theo = SampleDataFunctions.PostExpansion.StepDeltaChangeConstraint
    //TODO
//    val Experimental = DeltaConstraintMatrix(SampleDataFunctions.PreExpansion.KSystemsMini)
  }

  }
}