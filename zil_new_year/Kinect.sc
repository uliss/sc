Kinect {
    // head
    var <headX = 0, <headY = 0, <headZ = 0;
    var <accHeadX = 0, <accHeadY = 0, <accHeadZ = 0;
    var head_tx, head_ty, head_tz;
    var dump_head;

    // left hand
    var <leftHandX = 0, <leftHandY = 0, <leftHandZ = 0;
    var <accLeftHandX = 0, <accLeftHandY = 0, <accLeftHandZ = 0;
    var handl_tx, handl_ty, handl_tz;
    var dump_left_hand;

    // right hand
    var <rightHandX = 0, <rightHandY = 0, <rightHandZ = 0;
    var <accRightHandX = 0, <accRightHandY = 0, <accRightHandZ = 0;
    var handr_tx, handr_ty, handr_tz;
    var dump_right_hand;


    *new {
        ^super.new.init
    }

    init {
        arg port = 10000;

        head_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accHeadX = v - headX;
            headX = v;
        }, "/p1/head:tx", nil, port);

        head_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accHeadY = v - headY;
            headY = v;
        }, "/p1/head:ty", nil, port);

        head_tz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accHeadZ = v - headZ;
            headZ = v;
        }, "/p1/head:tz", nil, port);

        dump_head = Routine{
            inf.do{
                format("HEAD: x=%,y=%,z=%, accX=%,accY=%,accZ=%", headX, headY, headZ, accHeadX, accHeadY, accHeadZ).postln;
                0.2.wait;
            }
        };

        dump_left_hand = Routine{
            inf.do{
                format("LEFT HAND: x=%,y=%,z=%, accX=%,accY=%,accZ=%", leftHandX, leftHandY, leftHandZ, accLeftHandX, accLeftHandY, accLeftHandZ).postln;
                0.2.wait;
            }
        };

        dump_right_hand = Routine{
            inf.do{
                format("RIGHT HAND: x=%,y=%,z=%, accX=%,accY=%,accZ=%", rightHandX, rightHandY, rightHandZ, accRightHandX, accRightHandY, accRightHandZ).postln;
                0.2.wait;
            }
        };

        handl_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandX = v - leftHandX;
            leftHandX = v;
        }, "/p1/hand_l:tx", nil, port);

        handl_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandY = v - leftHandY;
            leftHandY = v;
        }, "/p1/hand_l:ty", nil, port);

        handl_tz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandZ = v - leftHandZ;
            leftHandZ = v;
        }, "/p1/hand_l:tz", nil, port);

        handr_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandX = v - rightHandX;
            rightHandX = v;
        }, "/p1/hand_r:tx", nil, port);

        handr_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandY = v - rightHandY;
            rightHandY = v;
        }, "/p1/hand_r:ty", nil, port);

        handr_tz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandZ = v - rightHandZ;
            rightHandZ = v;
        }, "/p1/hand_r:tz", nil, port);
    }

    dumpHead { |run = true|
        if(run, {dump_head.reset; dump_head.play}, {dump_head.stop});
    }

    dumpLeftHand { |run = true|
        if(run, {dump_left_hand.reset; dump_left_hand.play}, {dump_left_hand.stop});
    }

    dumpRightHand { |run = true|
        if(run, {dump_right_hand.reset; dump_right_hand.play}, {dump_right_hand.stop});
    }
}