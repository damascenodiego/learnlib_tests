(define-sort Feature () Bool)
(declare-const f0 Feature)
(declare-const f1 Feature)
(declare-const f2 Feature)
(declare-const f3 Feature)
(declare-const f4 Feature)
(declare-const f5 Feature)
(declare-const f6 Feature)
(declare-const f7 Feature)
(declare-const f8 Feature)
(declare-const f9 Feature)
(declare-const f10 Feature)
(declare-const f11 Feature)
(declare-const f12 Feature)
(declare-const f13 Feature)
(declare-const f14 Feature)
(declare-const f15 Feature)
(declare-const f16 Feature)
(declare-const f17 Feature)
(declare-const f18 Feature)
(declare-const f19 Feature)
(declare-const f20 Feature)

(assert f0)
(assert (and
   (=> f1 f0)
   (=> f2 f1)
   (=> f3 f2)
   (=> f4 f3)
   (=> f5 f4)
   (=> f6 f5)
   (=> f7 f6)
   (=> f8 f7)
   (=> f9 f8)
   (=> f10 f9)
   (=> f11 f10)
   (=> f12 f11)
   (=> f13 f12)
   (=> f14 f13)
   (=> f15 f14)
   (=> f16 f15)
   (=> f17 f16)
   (=> f18 f17)
   (=> f19 f18)
   (=> f20 f19)
))

(push)
(assert f6)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 ))
(check-sat)
(pop)
(push)
(assert f15)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 f13 f14 f14 f15 ))
(check-sat)
(pop)
(push)
(assert f2)
(assert (and f0 f1 f1 f2 ))
(check-sat)
(pop)
(push)
(assert f20)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 f13 f14 f14 f15 f15 f16 f16 f17 f17 f18 f18 f19 f19 f20 ))
(check-sat)
(pop)
(push)
(assert f19)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 f13 f14 f14 f15 f15 f16 f16 f17 f17 f18 f18 f19 ))
(check-sat)
(pop)
(push)
(assert f11)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 ))
(check-sat)
(pop)
(push)
(assert f17)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 f13 f14 f14 f15 f15 f16 f16 f17 ))
(check-sat)
(pop)
(push)
(assert f4)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 ))
(check-sat)
(pop)
(push)
(assert f1)
(assert (and f0 f1 ))
(check-sat)
(pop)
(push)
(assert f8)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 ))
(check-sat)
(pop)
(push)
(assert f16)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 f13 f14 f14 f15 f15 f16 ))
(check-sat)
(pop)
(push)
(assert f7)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 ))
(check-sat)
(pop)
(push)
(assert f12)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 ))
(check-sat)
(pop)
(push)
(assert f14)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 f13 f14 ))
(check-sat)
(pop)
(push)
(assert f3)
(assert (and f0 f1 f1 f2 f2 f3 ))
(check-sat)
(pop)
(push)
(assert f5)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 ))
(check-sat)
(pop)
(push)
(assert f9)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 ))
(check-sat)
(pop)
(push)
(assert f13)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 ))
(check-sat)
(pop)
(push)
(assert f10)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 ))
(check-sat)
(pop)
(push)
(assert f18)
(assert (and f0 f1 f1 f2 f2 f3 f3 f4 f4 f5 f5 f6 f6 f7 f7 f8 f8 f9 f9 f10 f10 f11 f11 f12 f12 f13 f13 f14 f14 f15 f15 f16 f16 f17 f17 f18 ))
(check-sat)
(pop)