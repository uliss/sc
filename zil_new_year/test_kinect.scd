(
// k = Kinect.new(1);
~k1 = Kinect.new(1);
~k2 = Kinect.new(2);
)

~k2.dumpHead(true);
~k2.dumpHead(false);

k.dumpLeftHand(true);
k.dumpLeftHand(false);

k.dumpRightHand(true);
k.dumpRightHand(false);

k.dumpLeftHandTip(true);
k.dumpLeftHandTip(false);

/*r.stop;
r.reset;
r.play*/

n.sendMsg("/gadanie/0", 1);
n.sendMsg("/gadanie/0", 0);

n.sendMsg("/gadanie/1", 1);
n.sendMsg("/gadanie/1", 0);

(
~k1 = Kinect.new(1);
~k2 = Kinect.new(2);

n = NetAddr("127.0.0.1", 7000);
r = Routine {
    inf.do {
        var p1_lh_acc_x = ~k1.accLeftHandTipX.lag(0.1).abs;
        var p1_lh_acc_y = ~k1.accLeftHandTipY.lag(0.1).abs;
        var p1_rh_acc_x = ~k1.accRightHandX.lag(0.1).abs;
        var p1_rh_acc_y = ~k1.accRightHandY.lag(0.1).abs;
        var p1_head_x = ~k1.accHeadX.lag(0.1).abs;
        var p1_head_y = ~k1.accHeadY.lag(0.1).abs;
        var p1_acc_mean = [p1_lh_acc_x, p1_lh_acc_y, p1_rh_acc_x, p1_rh_acc_y, p1_head_x, p1_head_y].maxItem;

        var p2_lh_acc_x = ~k2.accLeftHandTipX.lag(0.1).abs;
        var p2_lh_acc_y = ~k2.accLeftHandTipY.lag(0.1).abs;
        var p2_rh_acc_x = ~k2.accRightHandX.lag(0.1).abs;
        var p2_rh_acc_y = ~k2.accRightHandY.lag(0.1).abs;
        var p2_head_x = ~k2.accHeadX.lag(0.1).abs;
        var p2_head_y = ~k2.accHeadY.lag(0.1).abs;
        var p2_acc_mean = [p2_lh_acc_x, p2_lh_acc_y, p1_rh_acc_x, p1_rh_acc_y, p2_head_x, p2_head_y].maxItem;
        // var acc = k.accLeftHandX.lag(0.1);
        // k.accLeftHandX.postln;

        // n.sendMsg("/gadanie/0", \amp, acc.linlin(-0.1, 0.1, 0, 1));
        // 0.01 bad
        // 0.02, 0.007
        if(p1_acc_mean > 0.02) {
            n.sendMsg("/gadanie/0", 1);
            p1_acc_mean.postln;
            "P1 gadanie ON".postln;
        };

        if(p1_acc_mean < 0.007) {
            n.sendMsg("/gadanie/0", 0);
            p1_acc_mean.postln;
            // "P1 gadanie OFF".postln;
        };

        if(p2_acc_mean > 0.02) {
            n.sendMsg("/gadanie/1", 1);
            p2_acc_mean.postln;
            "P2 gadanie ON".postln;
        };

        if(p2_acc_mean < 0.007) {
            n.sendMsg("/gadanie/1", 0);
            p2_acc_mean.postln;
            // "P2 gadanie OFF".postln;
        };


        0.2.wait;
    }
};

r.play;
)

r.reset;
r.stop;


(
// k = Kinect.new;
~k1 = Kinect.new(1);
// ~k2 = Kinect.new(2);
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var acc = ~k1.accLeftHandX;

        var pos = ~k1.headX.linlin(-3, 3, -1, 1);
        // k.headX.postln.lag(0.5);

        // ~k1.headX.postln;

        if(acc > 0.01 && started.not) {
            "DVOINIK START".postln;
            n.sendMsg("/dvoinik", \start);
            started = true;
        };

        // поиск двойника
        n.sendMsg("/dvoinik", \pos1, -1 * pos);
        // n.sendMsg("/dvoinik", \pos1, pos);

        0.3.wait;
    }
};

r.play;
)

n.sendMsg("/dvoinik", \stop);
r.reset;
r.stop;


(
k = Kinect.new(1);
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var pos = k.headZ.linlin(1.5, 4, -1, 1);
        // k.headZ.postln.lag(0.5);
        // k.headZ.postln


        n.sendMsg("/xfader", \pos, pos.lag(0.5));
        0.1.wait;
    }
};

r.play;
)




n.sendMsg("/xfader", \start);
n.sendMsg("/xfader", \stop);
r.reset;
r.stop;


(
k = Kinect.new(1);
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var pos = k.headZ.linlin(1.5, 4, -1, 1);
        // k.headZ.postln.lag(0.5);
        // k.headZ.postln


        n.sendMsg("/xfader", \pos, pos.lag(0.5));
        0.1.wait;
    }
};

r.play;
)

(
k = Kinect.new(1);
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var acc = k.accX();
        // k.headZ.postln.lag(0.5);
        // k.headZ.postln
        if(acc > 0.05) {
            var ch = [1,2,3].choose;

            switch(ch,
                1, {
                     Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("metal1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                },
                2, {
                    Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("onion1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                },
                3, {
                    Synth(\sample_beat, [\amp, 5, \buf, l.buffer("onion2"), \pos, 2.0.rand - 1, startPos: 5000.rand, \dur, 1]);
                }
            );
        };



        // n.sendMsg("/xfader", \pos, pos.lag(0.5));
        0.3.wait;
    }
};

r.play;
)

