OscPeak : UGen {
    *kr { arg in = 0.0, oscPath = '/OscPeak', lagU = 0, lagD = 3;
        var imp, delimp;
        imp = Impulse.kr(5);
        delimp = Delay1.kr(imp);

        // send level via OSC
        SendReply.kr(imp, oscPath, [
            K2A.ar(Peak.ar(in, delimp).lag(lagU, lagD));
        ], 1905);

        ^0.0
	}
}

OscAmplitude : UGen {
    *kr { arg in = 0.0, oscPath = '/OscAmplitude', lag = 0;
        var imp, delimp;
        imp = Impulse.kr(5);
        delimp = Delay1.kr(imp);

        SendReply.kr(imp, oscPath, [
            Amplitude.kr(in).lag(lag);
        ], 1905);

        ^0.0
	}
}

OscVU : UGen {
    *kr { arg in = 0.0, oscPath = '/OscVU';
        var imp, delimp;
        imp = Impulse.kr(5);
        delimp = Delay1.kr(imp);

        SendReply.kr(imp, oscPath, [
            Amplitude.kr(in) ++
            K2A.ar(Peak.ar(in, delimp).lag(0, 3))
        ], 1905);

        ^0.0
	}
}
