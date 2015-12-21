(
// k = Kinect.new(1);
~k1 = Kinect.new(1);
~k2 = Kinect.new(2);
)

~k2.dump(\head, true);
~k2.dump(\head, false);

~k1.dump(\left_hand, true);
~k1.dump(\left_hand, false);

~k1.dump(\right_hand, true);
~k1.dump(\right_hand, false);

~k1.dump(\left_hand_tip, true);
~k1.dump(\left_hand_tip, false);

/*r.stop;
r.reset;
r.play*/

(
r = Routine{
  inf.do {
        ~k1.headX.postln;
        1.wait;
    }
};

r.play
)

n.sendMsg("/gadanie/0", 1);
n.sendMsg("/gadanie/0", 0);

n.sendMsg("/gadanie/1", 1);
n.sendMsg("/gadanie/1", 0);

~k1.dump("hand_left", false);

(
~k1 = Kinect.new(1);
~k2 = Kinect.new(2);



n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var p1_on = false;
    var p2_on = false;

    /*var on_thresh = 0.035;
    var off_thres = 0.034;*/

    var on_thresh = 0.07;
    var off_thres = 0.041;

    inf.do {
        //  ~k1.accHead

        var p1_acc =  ~k1.accTop.lag(0.2);
        // var p1_acc = ~k1.accAll;
        // var p1_acc = ~k1.accHands.lag(0.5); // 0.35-0.34
        // var p1_acc = ~k1.accLeftHand;
        //[~k1.accLeftHandTip, ~k1.accRightHandTip].maxItem; //~k1.accHands.lag(0.1);
        // var p2_acc = 0;// ~k2.accHands.lag(0.1);
        var p2_acc = ~k2.accTop.lag(0.2);
        // var p2_acc = ~k2.accHands;
        // var p2_acc = ~k2.accRightHand;

        // p1_acc.postln;



        // 0.01 bad
        // 0.02, 0.007
        if(p1_acc > on_thresh) {
            p1_on = true;
            n.sendMsg("/gadanie/0", 1);
            format("P1 gadanie ON: %", p1_acc).postln;
        };

        if(p1_on && (p1_acc < off_thres)) {
            p1_on = false;
            n.sendMsg("/gadanie/0", 0);
            format("P1 gadanie OFF: %", p1_acc).postln;
        };

        if(p2_acc > on_thresh) {
            p2_on = true;
            n.sendMsg("/gadanie/1", 1);
            format("P2 gadanie ON: %", p2_acc).postln;
        };

        if(p2_on && (p2_acc < (off_thres * 0.9)))  {
            p2_on = false;
            n.sendMsg("/gadanie/1", 0);
            format("P2 gadanie OFF: %", p2_acc).postln;
        };

/*        if(~k1.noHands()) {
            p1_on = false;
            n.sendMsg("/gadanie/0", 0);
        };

        if(~k2.noHands()) {
            p2_on = false;
            n.sendMsg("/gadanie/1", 0);
        };*/


        0.05.wait;
    }
};

r.play;
)

r.reset;
r.stop;


(
// k = Kinect.new;
var k1 = Kinect.new(1);
// ~k2 = Kinect.new(2);
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var acc = k1.accLeftHandX;

        var pos = k1.headX.linlin(-2, 2, -1, 1);
        k1.headX.postln.lag(0.5);

        // ~k1.headX.postln;

        if(acc > 0.015 && started.not) {
            "DVOINIK START".postln;
            n.sendMsg("/dvoinik", \start);
            started = true;
        };

        // поиск двойника
        if(acc != 0) {
            n.sendMsg("/dvoinik", \pos1, pos);
        };
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
k =
k = Kinect.new(1);
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var acc = k.accAll;
        // k.headZ.postln.lag(0.5);
        // k.headZ.postln
        if(acc > 0.07) {
            var ch = [1,2,3,4].choose;

            switch(ch,
                1, {
                     Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("metal1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                },
                2, {
                    Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("onion1"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                },
                3, {
                    Synth(\sample_beat, [\amp, 5, \buf, l.buffer("onion2"), \pos, 2.0.rand - 1, startPos: 5000.rand, \dur, 1]);
                },
                4, {
                     Synth(\sample_beat, [\amp, 1.3, \buf, l.buffer("metal4"), \pos, 2.0.rand - 1, startPos: 10000.rand, \dur, [0.5, 2, 1].choose]);
                },
            );
        };



        // n.sendMsg("/xfader", \pos, pos.lag(0.5));
        0.3.wait;
    }
};

r.play;
)

r.stop;

