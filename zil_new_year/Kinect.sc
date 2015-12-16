Kinect {
    var personId;

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

    // left hand tip
    var <leftHandTipX = 0, <leftHandTipY = 0, <leftHandTipZ = 0;
    var <accLeftHandTipX = 0, <accLeftHandTipY = 0, <accLeftHandTipZ = 0;
    var handtipl_tx, handtipl_ty, handtipl_tz;
    var dump_left_hand_tip;


    // right hand
    var <rightHandTipX = 0, <rightHandTipY = 0, <rightHandTipZ = 0;
    var <accRightHandX = 0, <accRightHandY = 0, <accRightHandZ = 0;
    var handr_tx, handr_ty, handr_tz;
    var dump_right_hand;

     // right hand tip
    var <rightHandTipX = 0, <rightHandTipY = 0, <rightHandTipZ = 0;
    var <accRightHandTipX = 0, <accRightHandTipY = 0, <accRightHandTipZ = 0;
    var handtipr_tx, handtipr_ty, handtipr_tz;
    var dump_right_hand_tip;


    *new {
        arg id;
        ^super.new.init(person: id)
    }

    oscP {
        arg name;
        ^"/p" ++ personId ++ "/" ++ name;
    }

    init {
        arg port = 10000, person = 1;
        personId = person;

        head_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accHeadX = v - headX;
            headX = v;
        }, this.oscP("head:tx"), nil, port);

        head_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accHeadY = v - headY;
            headY = v;
        }, this.oscP("head:ty"), nil, port);

        head_tz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accHeadZ = v - headZ;
            headZ = v;
        }, this.oscP("head:tz"), nil, port);

        dump_head = Routine{
            inf.do{
                format("PERSON" ++ personId + "HEAD: x=%,y=%,z=%, accX=%,accY=%,accZ=%", headX, headY, headZ, accHeadX, accHeadY, accHeadZ).postln;
                0.2.wait;
            }
        };

        dump_left_hand = Routine{
            inf.do{
                format("PERSON" ++ personId + "LEFT HAND: x=%,y=%,z=%, accX=%,accY=%,accZ=%", leftHandX, leftHandY, leftHandZ, accLeftHandX, accLeftHandY, accLeftHandZ).postln;
                0.2.wait;
            }
        };

        dump_right_hand = Routine{
            inf.do{
                format("PERSON" ++ personId + "RIGHT HAND: x=%,y=%,z=%, accX=%,accY=%,accZ=%", rightHandX, rightHandY, rightHandZ, accRightHandX, accRightHandY, accRightHandZ).postln;
                0.2.wait;
            }
        };

        dump_left_hand_tip = Routine{
            inf.do{
                format("PERSON" ++ personId + "LEFT HAND TIP: x=%,y=%,z=%, accX=%,accY=%,accZ=%", leftHandTipX, leftHandTipY, leftHandTipZ, accLeftHandTipX, accLeftHandTipY, accLeftHandTipZ).postln;
                0.2.wait;
            }
        };

        handl_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandX = v - leftHandX;
            leftHandX = v;
        }, this.oscP("hand_l:tx"), nil, port);

        handl_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandY = v - leftHandY;
            leftHandY = v;
        }, this.oscP("hand_l:ty"), nil, port);

        handl_tz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandZ = v - leftHandZ;
            leftHandZ = v;
        }, this.oscP("hand_l:tz"), nil, port);


        handtipl_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandTipX = v - leftHandTipX;
            leftHandTipX = v;
        }, this.oscP("handtip_l:tx"), nil, port);

        handtipl_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftHandTipY = v - leftHandTipY;
            leftHandTipY = v;
        }, this.oscP("handtip_l:ty"), nil, port);


        handr_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandX = v - rightHandX;
            rightHandX = v;
        }, this.oscP("hand_r:tx"), nil, port);

        handr_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandY = v - rightHandY;
            rightHandY = v;
        }, this.oscP("hand_r:ty"), nil, port);

        handr_tz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandZ = v - rightHandZ;
            rightHandZ = v;
        }, this.oscP("hand_r:tz"), nil, port);

        handtipr_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandTipX = v - rightHandTipX;
            rightHandTipX = v;
        }, this.oscP("handtip_r:tx"), nil, port);

        handtipr_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightHandTipY = v - rightHandTipY;
            rightHandTipY = v;
        }, this.oscP("handtip_r:ty"), nil, port);
    }

    // @returns true on every hand movement
    // false otehrwise
    noHands {
        ^[accLeftHandX, accRightHandX, accLeftHandTipX, accRightHandTipX,
            accLeftHandY, accRightHandY, accLeftHandTipY, accRightHandTipY,
            accLeftHandZ, accRightHandZ, accLeftHandTipZ, accRightHandTipZ].every({
            arg item;
            item == 0;
        });
    }

    handsAccX {
        ^[accLeftHandX, accRightHandX, accLeftHandTipX, accRightHandTipX].abs.maxItem;
    }

    handsAccY {
        ^[accLeftHandY, accRightHandY, accLeftHandTipY, accRightHandTipY].abs.maxItem;
    }

    handsAccZ {
        ^[accLeftHandZ, accRightHandZ, accLeftHandTipZ, accRightHandTipZ].abs.maxItem;
    }

    handsAccAny {
        max(this.handsAccX, this.handsAccY, this.handsAccZ);
    }

    headAccAny {
        [^accHeadX, accHeadY, accHeadZs];
    }

    accX {
        ^[accHeadX, accLeftHandX, accRightHandX, accLeftHandTipX, accRightHandTipX].maxItem;
    }

    accY {
        ^[accHeadY, accLeftHandY, accRightHandY, accLeftHandTipY, accRightHandTipY].maxItem;
    }

    dumpHead { |run = true|
        if(run, {dump_head.reset; dump_head.play}, {dump_head.stop});
    }

    dumpLeftHand { |run = true|
        if(run, {dump_left_hand.reset; dump_left_hand.play}, {dump_left_hand.stop});
    }

    dumpLeftHandTip { |run = true|
        if(run, {dump_left_hand_tip.reset; dump_left_hand_tip.play}, {dump_left_hand_tip.stop});
    }

    dumpRightHand { |run = true|
        if(run, {dump_right_hand.reset; dump_right_hand.play}, {dump_right_hand.stop});
    }
}