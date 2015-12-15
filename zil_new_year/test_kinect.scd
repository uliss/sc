(
k = Kinect.new;
)

k.dumpHead(true);
k.dumpHead(false);

k.dumpLeftHand(true);
k.dumpLeftHand(false);

k.dumpRightHand(true);
k.dumpRightHand(false);

/*r.stop;
r.reset;
r.play*/

n.sendMsg("/gadanie/0", 1);
n.sendMsg("/gadanie/0", 0);

(
k = Kinect.new;
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    inf.do {
        k.accLeftHandX.postln;

        if(k.accLeftHandX > 0.01) {
            n.sendMsg("/gadanie/0", 1);
            "gadanie ON".postln;
        };

        if(k.accLeftHandX < 0.01) {
            n.sendMsg("/gadanie/0", 0);
            "gadanie OFF".postln;
        };

        0.1.wait;
    }
};

r.play;
)

r.reset;
r.stop;


(
k = Kinect.new;
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var acc = k.accLeftHandX;
        var pos = k.headX.linlin(-5, 5, -1, 1);
        k.headX.postln.lag(0.5);

        if(acc > 0.01 && started.not) {
            "DVOINIK START".postln;
            n.sendMsg("/dvoinik", \start);
            started = true;
        }

        n.sendMsg("/dvoinik", \pos1, pos);
        // n.sendMsg("/dvoinik", \pos1, pos);

        0.1.wait;
    }
};

r.play;
)

n.sendMsg("/dvoinik", \stop);
r.reset;
r.stop;


(
k = Kinect.new;
n = NetAddr("127.0.0.1", 7000);
r = Routine {
    var started = false;

    inf.do {
        var pos = k.headZ.linlin(0, 5, -1, 1);
        k.headX.postln.lag(0.5);


        n.sendMsg("/xfader", \fade, pos.lag(0.5));
        0.1.wait;
    }
};

r.play;
)

n.sendMsg("/xfader", \start);
n.sendMsg("/xfader", \stop);
r.reset;
r.stop;

