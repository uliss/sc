SPU_VinylNoise1 : UGen {
    *ar {
        var	sig, sig1, sig2, lpf, popHz, lagtime, noise, popHzMul,
		pan1, pan2, panmod1, panmod2;
        popHzMul = Decay.kr(Dust.kr(0.15), 3, 10, 0.8);
        popHz = 	LFNoise1.kr(20).exprange(0.1,10) * popHzMul;
        sig = Dust2.ar(popHz);
        lpf = LFNoise1.kr(10).exprange(1000,20000);
        lagtime = LFNoise1.kr(20).range(0.008,0.0001);
        sig = LPF.ar(sig, lpf);
        sig = Lag.ar(sig, lagtime);
        sig = sig + FreeVerb.ar(sig, 0.8, 1, mul:0.11);
        panmod1 = LFNoise1.kr(5).range(0.2,0.7);
        panmod2 = LFNoise1.kr(5).range(0.2,0.7);
        pan1 = SinOsc.kr(panmod1).range(-0.2,0.2);
        pan2 = SinOsc.kr(panmod2).range(-0.2,0.2);
        sig1 = Pan2.ar(sig, pan1, 0.5);
        sig2 = Pan2.ar(sig, pan2, 0.5);
        sig = sig1 + sig2;
        sig = sig + BPF.ar(BrownNoise.ar([0.0025,0.0025]), 7200, 0.4);
        sig = sig + HPF.ar(Crackle.ar([1.999,1.999], 0.0025),2000);
        ^sig * 6;
    }
}

SPU_VinylNoise2 : UGen {
    *ar {
        var sig1, sig;
        sig1 = Dust2.ar(1,0.8)+Dust2.ar(7,0.4)+Dust2.ar(17,0.2)+Dust2.ar(33,0.1)+Dust2.ar(66,0.05);
        sig = BPF.ar(sig1, 900)!2;
        ^sig;
    }
}















