package bbdl.space
import java.io.File
import breeze.linalg._
import breeze.numerics._
import breeze.math._
import org.scalatest._
import Matchers._
import scala.util.Random

class MaximumOutputSpec extends FlatSpec with Matchers {
  behavior of "Maximum Output Spec"
    val A = DenseMatrix(
      (10.0/3.0, -53.0/15.0, 2.0)
    )
    val b = DenseVector(1.0)
    val MaxInfo = MaximumOutput(A,b)
    val FOutputVector = MaxInfo._1
    val ActivationVec = MaxInfo._2
  it should "Get correct maximum force vector" in {
//    16.0/3.0 is 5.333333333333334
    assert(FOutputVector == DenseVector(5.333333333333334))
  }
  it should "Get correct activation vector" in {
    assert(ActivationVec == DenseVector(1.0,0.0,1.0))
  }
  //set up the A matrix for the index finger in 7 d
  val JRIndex = DenseMatrix(
    (-0.08941, -0.0447, 0.2087, -0.2138, -0.009249, 0.1421, 0.03669),
    (-0.04689, -0.1496, 0.0, 0.0248, 0.052, 0.0248, 0.052),
    (0.06472, 0.001953, 0.0568, 0.2067, -0.1518, 0.2919, -0.1518),
    (0.003081, -0.002352, 0.0001578, -0.000685, -0.0001649, -0.0004483, -0.0001649)
  )
  val FmIndex = DenseVector(123,219,124.8,129.6,23.52,21.6,91.74)
  val AIndex = JRIndex*diag(FmIndex)
  val bIndex_y = DenseVector(0.0,1.0,0.0,0.0)//this is the direction we will maximize into.
  val IndexMaxInfo = MaximumOutput(AIndex, bIndex_y)
  val FOutputVectorIndex = IndexMaxInfo._1
  val IndexAVec = IndexMaxInfo._2
  it should "Get correct maximum force vector where y is maximized on the FVC index finger. in 7d" in {
    val diff = AbsDiff.Vectors(FOutputVectorIndex, DenseVector(0.0, 6.757881713139706, 0.0, 0.0))
    assert(diff < 1E-13)//pasted output - test generated for sustainability tracking
  }
  it should "Get correct activation vector where y is maximized on the FVC index finger in 7D." in {
    val diff = AbsDiff.Vectors(IndexAVec, DenseVector(0.14390467823812347, 0.0, 0.1724420617559178, 0.3293781199403045, 1.0, 1.0, 1.0))
    assert(diff < 1E-13)
  }
  it should "Generate force when using the a solution and the Fm matrix" in {
    val ExpF = AIndex*IndexAVec
    val AbsError = AbsDiff.Matricies(ExpF.toDenseMatrix, FOutputVectorIndex.toDenseMatrix)
    assert(AbsError < 1E-12)
  }
  it should "Get correct maximum force vector where z is maximized on the FVC index finger in 7d" in {
    val bIndex_z = DenseVector(0.0,0.0,1.0,0.0)
    val IndexMaxInfo_z = MaximumOutput(AIndex, bIndex_z)
    val diff = AbsDiff.Vectors(IndexMaxInfo_z._1, DenseVector(0.0, 0.0, 40.28419614785415, 0.0))
    assert(diff < 1E-13)
  }
  it should "Get correct activation vector where z is maximized on the FVC index finger in 7d" in {
    val bIndex_z = DenseVector(0.0,0.0,1.0,0.0)
    val IndexMaxInfo_z = MaximumOutput(AIndex, bIndex_z)
    val diff = AbsDiff.Vectors(IndexMaxInfo_z._2, DenseVector(0.27027228376106427, 0.05927684709966398, 1.0, 0.922552974363257, 0.0, 1.0, 0.0))
    assert(diff <= 1E-14)
  }
  it should "Get correct maximum force vector where y and z is maximized on the FVC index finger in 7d" in {
    val bIndex_yz = DenseVector(0.0,1.0,1.0,0.0)
    val IndexMaxInfo_yz = MaximumOutput(AIndex, bIndex_yz)
    val diff = AbsDiff.Vectors(IndexMaxInfo_yz._1, DenseVector(0.0, 7.195388104572386, 7.195388104572386, 0.0))
    assert(diff < 1E-13)
  }
  it should "Get correct activation vector where y and z is maximized on the FVC index finger in 7d" in {
    val bIndex_yz = DenseVector(0.0,1.0,1.0,0.0)
    val IndexMaxInfo_yz = MaximumOutput(AIndex, bIndex_yz)
    val diff = AbsDiff.Vectors(IndexMaxInfo_yz._2, DenseVector(0.17866959252521905, 0.0, 0.3982986123112096, 0.5278834439011518, 1.0, 1.0, 0.9999999999999999))
    assert(diff < 1E-14)
  }
}

class BasisSpec extends FlatSpec with Matchers {
	behavior of "Basis"
	it should "take in a matrix of size (2,5), and output a (5,2) basis" in {
    import bbdl.space.Basis
    import breeze.linalg._
    val A = DenseMatrix(
      (1.0, 1.0, 0.0, 0.0, 1.0),
      (0.0, 1.0, 1.0, 1.0, 1.0),
      (1.0, 0.0, 2.0, 1.0, 1.0)
    )
    val basis = Basis(A)
    val ExpectedBasis = DenseMatrix((1,0),(0,1), (-1,1),(2, -1),(-1, -1))
    assert(basis == ExpectedBasis)
	}
  it should "take in a matrix of size(2,4) and output a (4,2) basis" in {
    val A = DenseMatrix(
      (1.0, 1.0, 1.0, 1.0),
      (1.0, 1.0, 2.0, 1.0)
    )
    val basis=Basis(A)
    val ExpectedBasis = DenseMatrix(
      (1.0, 0.0),
      (0.0,1.0),
      (0.0,0.0),
      (-1.0,-1.0)
    )
    assert(basis === ExpectedBasis)
  }
  it should "take in a matrix of size(1,3) and output a (3,2) basis" in
    { val A = DenseMatrix(
      (10.0/3.0, -53.0/15.0, 2.0)
    )
      val basis=Basis(A)
      val ExpectedBasis = DenseMatrix(
        (1.0, 0.0),
        (0.0,1.0),
        (-5.0/3.0,53.0/30.0)
      )
      assert(basis === ExpectedBasis)
    }

}

class OrthoSpec extends FlatSpec with Matchers {
	behavior of "orthonormalize"
	it should "take in a matrix of size (3,3); the basis" in {
    val BasisOrthonormal = Ortho( DenseMatrix((1.0,1.0),(0.0,1.0)) )
	  val ExpectedBasisOrthonormal = DenseMatrix((1.0,0.0), (0.0,1.0))
	  assert(BasisOrthonormal === ExpectedBasisOrthonormal)

	}
  it should "take in a matrix of size (3,2) and output a (3,2) orthogonal basis" in {
    val A = DenseMatrix(
    (1.0, 0.0),
    (0.0,1.0),
    (-5.0/3.0,53.0/30.0)
    )
    val Orthobasis = Ortho(A)
    import breeze.linalg.{DenseVector, norm, DenseMatrix}
    val a = norm(DenseVector(53.0/68.0, 1.0, 159.0/340.0))
    val ExpectedOrthoBasis = DenseMatrix(
      (3.0/sqrt(34.0), 53.0/(68.0*a)),
      (0.0,1.0/a),
      (-5.0/sqrt(34.0),159.0/(340.0*a))
    )
    val AbsError = AbsDiff.Matricies(Orthobasis, ExpectedOrthoBasis)
    assert(AbsError < 1E-14)
  }
}

class GetNewPointSpec extends FlatSpec with Matchers {
  val Seed = 10
  "GetEndpoints" should "Get endpoints for a point and a positive direction" in {
    val p = DenseVector(0.5,0.5,0.5)
    val q = DenseVector(-1.0,-2.0,1.0)
    val Endpoints = GetNewPoint.GetEndpoints(p,q)
    val ExpectedEndpoints = (DenseVector(0.75,1.0,0.25), DenseVector(0.25,0.0,0.75))
    assert(Endpoints === ExpectedEndpoints)
  }

  "GetEndpoints" should "Get endpoints for a point and a negative direction" in {
    val p = DenseVector(0.0,0.5,0.5)
    val q = DenseVector(2.0,1.0,2.0)
    val Endpoints = GetNewPoint.GetEndpoints(p,q)
    val ExpectedEndpoints = (DenseVector(0.0,0.5,0.5), DenseVector(0.5,0.75,1.0))
    assert(Endpoints === ExpectedEndpoints)
  }

  behavior of "UpperboundVal"
  it should "take in a 3d point and positive 3d direction, and output a 3d upperbound vector" in {
    val p = DenseVector(0.0,0.5,0.5)
    val q = DenseVector(2.0,1.0,2.0)
    val UpperBounds = GetNewPoint.GetUpperBoundVector(p,q)
    val ExpectedUpperBounds = DenseVector(0.5,0.5,0.25)
    assert(UpperBounds === ExpectedUpperBounds)
  }
  behavior of "LowerboundVal"
  it should "take in a 3d point and positive 3d direction, and output a 3d LowerBound vector" in {
    val p = DenseVector(0.0,0.5,0.5)
    val q = DenseVector(2.0,1.0,2.0)
    val UpperBounds = GetNewPoint.GetLowerBoundVector(p,q)
    val ExpectedUpperBounds = DenseVector(0.0,-0.5,-0.25)
    assert(UpperBounds === ExpectedUpperBounds)
  }
  behavior of "UpperboundVal"
  it should "take in a 3d point and negative 3d direction, and output a 3d upperbound vector" in {
    val p = DenseVector(0.5,0.5,0.5)
    val q = DenseVector(-1.0,-2.0,1.0)
    val UpperBounds = GetNewPoint.GetUpperBoundVector(p,q)
    val ExpectedUpperBounds = DenseVector(0.5,0.25,0.5)
    assert(UpperBounds === ExpectedUpperBounds)
  }
  behavior of "LowerboundVal"
  it should "take in a 3d point and negative 3d direction, and output a 3d LowerBound vector" in {
    val p = DenseVector(0.5,0.5,0.5)
    val q = DenseVector(-1.0,-2.0,1.0)
    val UpperBounds = GetNewPoint.GetLowerBoundVector(p,q)
    val ExpectedUpperBounds = DenseVector(-0.5,-0.25,-0.5)
    assert(UpperBounds === ExpectedUpperBounds)
    assert(UpperBounds === ExpectedUpperBounds)
    assert(UpperBounds === ExpectedUpperBounds)
    assert(UpperBounds === ExpectedUpperBounds)
  }
  //Set up a test for upper and lower bounds

  val Lowers = DenseVector(-1.0,-6.0,-4.0,-5.0,-3.0,-5.0)
  val Uppers = DenseVector(8.0,7.0,9.0,8.0,9.0,10.0)
  val PositiveBounds = GetNewPoint.GetBoundLimits(Uppers, Lowers)
  "GetBoundLimits" should "Return the max value within the lower bounds vector " in {
    assert(PositiveBounds._2 === -1.0)
  }
  it should "Return the minimum value within the upper bounds vector" in {
    assert(PositiveBounds._1 === 7.0)
  }

  
  "FindEndpoints" should "use the p, negative direction q, and bound information to assemble two endpoints" in {
    val p = DenseVector(0.5,0.5,0.5)
    val q = DenseVector(-1.0,-2.0,1.0)
    val LowerBoundInner = -0.25
    val UpperBoundInner = 0.25
    val Endpoints = GetNewPoint.FindEndpoints(p,q, UpperBoundInner,LowerBoundInner)
    val ExpectedEndpoints = (DenseVector(0.75,1.0,0.25), DenseVector(0.25,0.0,0.75))
    assert(Endpoints === ExpectedEndpoints)
  }
  it should "use the p, positive direction q, and bound information to assemble two endpoints" in {
    val p = DenseVector(0.0,0.5,0.5)
    val q = DenseVector(2.0,1.0,2.0)
    val LowerBoundInner = 0.0
    val UpperBoundInner = 0.25
    val Endpoints = GetNewPoint.FindEndpoints(p,q, UpperBoundInner,LowerBoundInner)
    val ExpectedEndpoints = (DenseVector(0.0,0.5,0.5), DenseVector(0.5,0.75,1.0))
    assert(Endpoints === ExpectedEndpoints)
  }
}

class LowLevelSimplexSpec() extends FlatSpec with Matchers {
  behavior of "LowLevelSimplex"
  it should "take in a (3,2) matrix and vector of len 3, and 2len c, and return vector of len 2. " in {
    val A = DenseMatrix(
      (1.0,2.0),
      (1.0,1.0),
      (-1.0,0.0)
      )
    val b = DenseVector(4.0,3.0,0.0)
    val c = DenseVector(0.0,1.0)
    val x_expected = DenseVector(0.0,2.0)
    assert(LowLevelSimplex(A,b,c) == x_expected)
  }
}

class GenStartingPointSpec() extends FlatSpec with Matchers {
  behavior of "ExpandAMatrix"
  it should "take in a simple 2,3 matrix and output the expanded A" in {
    val A = DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0))
    // val G = DenseMatrix((1.0,2.0), (3.0,4.0))

    val AExpected = DenseMatrix(
      (1.0,2.0,3.0,0.0,0.0,0.0),
      (4.0,5.0,6.0,0.0,0.0,0.0),
      (-1.0,-2.0,-3.0,0.0,0.0,0.0),
      (-4.0,-5.0,-6.0,0.0,0.0,0.0),
      (-1.0,0.0,0.0,1.0,0.0,0.0),
      (0.0,-1.0,0.0,0.0,1.0,0.0),
      (0.0,0.0,-1.0,0.0,0.0,1.0),
      (1.0,0.0,0.0,1.0,0.0,0.0),
      (0.0,1.0,0.0,0.0,1.0,0.0),
      (0.0,0.0,1.0,0.0,0.0,1.0),
      (0.0,   0.0,   0.0,   -1.0,  -0.0,  -0.0),
      (0.0,   0.0,   0.0,   -0.0,  -1.0,  -0.0),
      (0.0,   0.0,   0.0,   -0.0,  -0.0,  -1.0) 
      )

    val myA = GenStartingPoint.ExpandAMatrix(A)
    assert(myA == AExpected)
  }
  behavior of "ExpandbVector"
  it should "take in a len 2 vector and output a len 10 vector expanded" in {
    val A = DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0))
    val ColNum = A.cols
    val b = DenseVector(7.0,8.0)
    val ExpandedbVector = GenStartingPoint.ExpandbVector(b, ColNum)
    val Expectedb= DenseVector(7.0,8.0,-7.0,-8.0,0.0,0.0,0.0,1.0,1.0,1.0,0.0,0.0,0.0)
    assert(ExpandedbVector == Expectedb)
  }
  behavior of "GencVector"
  it should "take in a 2,3 matrix and output the zeros and ones; a len 6 vector" in {
    val A = DenseMatrix((1.0,2.0,3.0),(4.0,5.0,6.0))
    val ColNum = A.cols
    val TestC = GenStartingPoint.GencVector(A)
    val Expectedc= DenseVector(0.0,0.0,0.0,1.0,1.0,1.0)
    assert(TestC == Expectedc)
  }
}

class UpdateMeanSpec() extends FlatSpec with Matchers{
  "updateMean" should "take in a prior mean (and an n) and update the running mean with a new value" in {
    val n = 5.0
    val PriorMean = 100.0
    val NewValue = 200.0
    val result = UpdateMean(NewValue, PriorMean, n)
    assert(result == 120.0)
  }
}

class BoundsSpec() extends FlatSpec with Matchers{
  "Bounds.Expanded Matrix" should "Properly form an expanded matrix from a 2,3 example" in {
    val A = DenseMatrix(
      (2.0,1.0,2.0),
      (1.0,1.0,3.0)
    )
    val res = Bounds.ExpandedMatrix(A)
    val expected = DenseMatrix(
      (2.0, 1.0, 2.0),
      (1.0, 1.0, 3.0),
      (-2.0, -1.0, -2.0),
      (-1.0, -1.0, -3.0),
      (-1.0, 0.0, 0.0),
      (0.0, -1.0, 0.0),
      (0.0, 0.0, -1.0),
      (1.0, 0.0, 0.0),
      (0.0, 1.0, 0.0),
      (0.0, 0.0, 1.0)
    )

    assert(res == expected)
  }
  "Bounds.ExpandedVector" should "Properly form an expanded vector from a 4 element example" in {
    val v = DenseVector(1.0,4.0,3.0)
    val ACols = 3
    val res = Bounds.ExpandedVector(ACols, v)
    val expected = DenseVector(1.0,4.0,3.0,-1.0,-4.0,-3.0, 0.0,0.0,0.0,1.0,1.0,1.0)
    assert(res == expected)
  }
  "Bounds.NumberAmongOnes" should "add a 1.0 between a bunch of zeros for a 5 element vector" in {
    assert(Bounds.NumberAmongZeros(1.0,5,2) == DenseVector(0.0,0.0,1.0,0.0,0.0))
  }
  val A = DenseMatrix(
    (10.0/3.0, -53.0/15.0, 2.0)
  )
  val v = DenseVector(1.0)
  "Bounds.ColBound" should "Be 1 for upperbound for a 1D output, 3D input, simple example" in {
    assert(Bounds.ColBound(A,v,0, "Upper") == 1.0)
    assert(Bounds.ColBound(A,v,1, "Upper") == 1.0)
    assert(Bounds.ColBound(A,v,2, "Upper") == 1.0)
  }
  "Bounds.ColBound" should "Be 0 for lowerbound for a 1D output, 3d,input, simple example" in {
    assert(Bounds.ColBound(A,v,0,"Lower") == 0.0)
    assert(Bounds.ColBound(A,v,1, "Lower") == 0.0)
    assert(Bounds.ColBound(A,v,2, "Lower") == 0.0)
  }
  "Bounds.ColBound" should "Be one value from"
  "Bounds.ComputeUppers" should "Computer the Upperbounds of 111 for the simple 3dinput,1D output example" in {
    assert(Bounds.ComputeUppers(A,v)== DenseVector(1.0,1.0,1.0))
  }
  "Bounds.ComputeLowers" should "Compute the Lowerbounds of 000 for the simple 3dinput, 1D output example" in {
    assert(Bounds.ComputeLowers(A,v)== DenseVector(0.0,0.0,0.0))
  }
}

class VectorScaleSpec() extends FlatSpec with Matchers{
  "VectorScale.apply" should "scale by fifty percent for a simple xy vector" in {
    val ScaledVector = VectorScale(DenseVector(1.0,0.0),0.5)
    assert(ScaledVector == DenseVector(0.5,0.0))
  }
}

class ExtrudeVectorSpec() extends FlatSpec with Matchers {
  "ExtrudeVector" should "Make the right size and shape of matrix" in {
    val v = DenseVector(1.0,1.0,2.0,0.0)
    val n = 2
    val res = ExtrudeVector(v,n)
    res should be (DenseMatrix((1.0,1.0,2.0,0.0),(1.0,1.0,2.0,0.0)))
  }
}

class VectorRepeatSpec() extends FlatSpec with Matchers {
  "VectorRepeat" should "turn a short vector into a repeated by-element vector" in {
    val v = DenseVector(0.2,0.4,0.6,0.8)
    val n = 1
    val res = VectorRepeat(v,n)
    res should be (v)
  }
  "VectorRepeat" should "turn a long vector into a repeated by-element vector" in {
    val v = DenseVector(0.2,0.4,0.6,0.8)
    val n = 2
    val res = VectorRepeat(v,n)
    res should be (DenseVector(0.2,0.2,0.4,0.4,0.6,0.6,0.8,0.8))
  }
}

class CostSpec() extends  FlatSpec with Matchers {
  behavior of "L1"
  val ExpV  = DenseVector(0.227166716,	0.110954506,	0.19195692,	0.134892534,	0.137534494,	0.805810655,	0.820020392)
  val ExpW = DenseVector(123,219,124.8,129.6,23.52,21.6,91.74)
  it should "return the simple sum of a vector of numbers" in {
    assert(Cost.L1Norm(DenseVector(1.0,2.0))==3.0)
    assert(Cost.L1Norm(DenseVector(1.0,2.0,3.0))==6.0)
    assert(Cost.L1Norm(DenseVector(1.0,8.0))==9.0)
  }
  "L2" should "get the pythagorean magnitude of the vector" in {
    assert(Cost.L2Norm(DenseVector(1.0,3.0,2.0))==sqrt(14))
  }
  "L3" should "multiply each element of the vector by its weight" in {
    assert(Cost.L3Norm(DenseVector(1.0,2.0,3.0))==cbrt(1.0+8.0+27.0))
  }
  "L1weighted" should "multiply each element of the vector by its weight" in {
    val v = DenseVector(1.0,2.0,3.0) //vector
    val w = DenseVector(2.0,2.0,1.0) //weightings array
    assert(Cost.L1WeightedNorm(v,w) == 9.0)
  }
  "L2weighted" should "multiply each element of the vector by its weight" in {
    val v = DenseVector(1.0,2.0,3.0) //vector
    val w = DenseVector(2.0,2.0,1.0) //weightings array
    assert(Cost.L2WeightedNorm(v,w) == sqrt(29.0))
  }
  "L3weighted" should "multiply each element of the vector by its weight" in {
    val v = DenseVector(1.0,2.0,3.0) //vector
    val w = DenseVector(2.0,2.0,1.0) //weightings array
    assert(Cost.L3WeightedNorm(v,w)== cbrt(99.0))
  }
  "L1-3 weighted and nonweighted" should "Work on experimental index finger data" in {
    val res = DenseVector(2.428336216,	1.208155232,	1.032237834,	189.547831,	90.68331237,	78.63872848)
    assert(Cost.L1Norm(ExpV)-res(0) < 1E-6)
    assert(Cost.L2Norm(ExpV)-res(1)< 1E-6)
    assert(Cost.L3Norm(ExpV)-res(2)< 1E-6)
    assert(Cost.L1WeightedNorm(ExpV,ExpW)- res(3)< 1E-6)
    assert(Cost.L2WeightedNorm(ExpV,ExpW)- res(4)< 1E-6)
    assert(Cost.L3WeightedNorm(ExpV,ExpW)- res(5)< 1E-6)
  }
}

