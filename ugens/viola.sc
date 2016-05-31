SPU_ViolaIn : UGen {
    *ar {
        arg in = 0;
        var ain = SoundIn.ar(in);
        ^ain;
    }
}

SPU_ViolaInTest : UGen {
    *ar {
        arg pos = 0, server = Server.default;
        var buf = Buffer.cueSoundFile(server, "~/work/music/sounds/viola_rec1.wav".standardizePath, pos * 44100, 1);
        ^DiskIn.ar(1, buf, 1);
    }
}

SPU_ViolaCompress : UGen {
    *ar {
        arg in, thresh = 0.5, slopeBelow = 1, slopeAbove = 1/3, clampTime = 0.002;
        var snd = Compander.ar(in, in, thresh, slopeBelow, slopeAbove, clampTime);
        ^snd;
    }
}

SPU_ViolaFilter : UGen {
    *ar {
        arg in, freq = 1000, amp = 1, warmFreq = 300, warmQ = 1.41,
            warmDb = 6, sharpFreq = 1200, sharpQ = 1.41, sharpDb = -5,
            formantFreq = 3000, formantQ = 1, formantDb = 8;

        // cut low freqs, lower then C grand octave
        var snd = HPF.ar(in, 47.midicps);
        // warm
        snd = BPeakEQ.ar(snd, warmFreq, warmQ, warmDb);
        // remove sharp
        snd = BPeakEQ.ar(snd, sharpFreq, sharpQ, sharpDb);
        // vocal formant
        snd = BPeakEQ.ar(snd, formantFreq, formantQ, formantDb);
        ^snd;
    }
}

SPU_ViolaPitch : UGen {
    *ar {
        arg in, path = "/viola/pitch";
        var freq, hasFreq;

        # freq, hasFreq = Tartini.kr(in, n: 1024, overlap: 512);
        SendReply.ar(Impulse.ar(10), path, [freq, freq.cpsmidi, hasFreq]);
        ^in;
    }
}

SPU_ViolaVU : UGen {
    *ar {
        arg in, path = "/viola/vu";
        SendPeakRMS.ar(in, 10, 2, path);
        ^in;
    }
}

SPU_ViolaReverb : UGen {
    *ar {
        arg in, mix = 0.5, room = 0.5, damp = 0.5;
        var snd = FreeVerb.ar(in, mix, room, damp);
        ^snd;
    }
}

SPU_ViolaInCommon : UGen {
    *ar {
        var snd;
        snd = SPU_ViolaIn.ar(0);
        ^SPU_ViolaVU.ar(SPU_ViolaReverb.ar(SPU_ViolaCompress.ar(SPU_ViolaFilter.ar(snd))));
    }
}

SPU_ViolaInCommonTest : UGen {
    *ar {
        arg pos = 0;
        var snd = SPU_ViolaInTest.ar(pos);
        ^SPU_ViolaVU.ar(SPU_ViolaReverb.ar(SPU_ViolaCompress.ar(SPU_ViolaFilter.ar(snd))));
    }
}

SPU_ViolaDelay2 : UGen {
    *ar {
        arg in, time = 1, maxtime = 1, pan1 = 0.4, pan2 = -0.4, amp1 = 1, amp2 = 1;
        var v1 = Pan2.ar(in, pan1) * amp1;
        var v2 = Pan2.ar(DelayC.ar(in, maxtime, time), pan2) * amp2;
        ^Mix.ar([v1, v2]);
    }
}