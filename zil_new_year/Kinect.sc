Kinect {
    var personId;
    var dumps;


    // head
    var <headX = 0, <headY = 0, <headZ = 0;
    var <accHeadX = 0, <accHeadY = 0, <accHeadZ = 0;
    var head_tx, head_ty, head_tz;

    // left hand
    var <leftHandX = 0, <leftHandY = 0, <leftHandZ = 0;
    var <accLeftHandX = 0, <accLeftHandY = 0, <accLeftHandZ = 0;
    var handl_tx, handl_ty, handl_tz;

    // right hand
    var <rightHandX = 0, <rightHandY = 0, <rightHandZ = 0;
    var <accRightHandX = 0, <accRightHandY = 0, <accRightHandZ = 0;
    var handr_tx, handr_ty, handr_tz;

    // left hand tip
    var <leftHandTipX = 0, <leftHandTipY = 0, <leftHandTipZ = 0;
    var <accLeftHandTipX = 0, <accLeftHandTipY = 0, <accLeftHandTipZ = 0;
    var handtipl_tx, handtipl_ty, handtipl_tz;

    // right hand
    var <rightHandTipX = 0, <rightHandTipY = 0, <rightHandTipZ = 0;
    var <accRightHandX = 0, <accRightHandY = 0, <accRightHandZ = 0;
    var handr_tx, handr_ty, handr_tz;

    // right hand tip
    var <rightHandTipX = 0, <rightHandTipY = 0, <rightHandTipZ = 0;
    var <accRightHandTipX = 0, <accRightHandTipY = 0, <accRightHandTipZ = 0;
    var handtipr_tx, handtipr_ty, handtipr_tz;

    // spine
    var <spineX = 0, <spineY = 0, <spineZ = 0;
    var <accSpineX = 0, <accSpineY = 0, <accSpineZ = 0;
    var spine_tx, spine_ty, spine_tz;

    // right knee
    var <rightKneeX = 0, <rightKneeY = 0, <rightKneeZ = 0;
    var <accRightKneeX = 0, <accRightKneeY = 0, <accRightKneeZ = 0;
    var knee_rx, knee_ry, knee_rz;

    // left knee
    var <leftKneeX = 0, <leftKneeY = 0, <leftKneeZ = 0;
    var <accLeftKneeX = 0, <accLeftKneeY = 0, <accLeftKneeZ = 0;
    var knee_lx, knee_ly, knee_lz;


    *new {
        arg id;
        ^super.new.init(person: id)
    }

    oscP {
        arg name;
        ^"/p" ++ personId ++ "/" ++ name;
    }

    init {
        arg port = 10000, person = 1, dump_timeout = 0.2;
        personId = person;
        dumps = Dictionary.new;

        /////////
        // HEAD
        /////////
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

        dumps[\head] = Routine{
            inf.do{
                format("PERSON" ++ personId + "HEAD: x=%,y=%,z=%, accX=%,accY=%,accZ=%", headX, headY, headZ, accHeadX, accHeadY, accHeadZ).postln;
                dump_timeout.wait;
            }
        };

        //////////
        // SPINE
        //////////
        spine_tx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accSpineX = v - spineX;
            spineX = v;
        }, this.oscP("spine:tx"), nil, port);

        spine_ty = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accSpineY = v - spineY;
            spineY = v;
        }, this.oscP("spine:ty"), nil, port);

        spine_tz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accSpineZ = v - spineZ;
            spineZ = v;
        }, this.oscP("spine:tz"), nil, port);

        dumps[\spine] = Routine{
            inf.do{
                format("PERSON" ++ personId + "SPINE: x=%,y=%,z=%, accX=%,accY=%,accZ=%", spineX, spineY, spineZ, accSpineX, accSpineY, accSpineZ).postln;
                dump_timeout.wait;
            }
        };

        //////////
        // HANDS
        //////////
        dumps[\left_hand] = Routine{
            inf.do{
                format("PERSON" ++ personId + "LEFT HAND: x=%,y=%,z=%, accX=%,accY=%,accZ=%", leftHandX, leftHandY, leftHandZ, accLeftHandX, accLeftHandY, accLeftHandZ).postln;
                dump_timeout.wait;
            }
        };

        dumps[\right_hand] = Routine{
            inf.do{
                format("PERSON" ++ personId + "RIGHT HAND: x=%,y=%,z=%, accX=%,accY=%,accZ=%", rightHandX, rightHandY, rightHandZ, accRightHandX, accRightHandY, accRightHandZ).postln;
                dump_timeout.wait;
            }
        };

        dumps[\left_hand_tip] = Routine{
            inf.do{
                format("PERSON" ++ personId + "LEFT HAND TIP: x=%,y=%,z=%, accX=%,accY=%,accZ=%", leftHandTipX, leftHandTipY, leftHandTipZ, accLeftHandTipX, accLeftHandTipY, accLeftHandTipZ).postln;
                dump_timeout.wait;
            }
        };

        dumps[\right_hand_tip] = Routine{
            inf.do{
                format("PERSON" ++ personId + "RIGHT HAND TIP: x=%,y=%,z=%, accX=%,accY=%,accZ=%", rightHandTipX, rightHandTipY, rightHandTipZ, accRightHandTipX, accRightHandTipY, accRightHandTipZ).postln;
                dump_timeout.wait;
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

        //////////
        // KNEES
        //////////

        // RIGHT KNEE
        knee_rx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightKneeX = v - rightKneeX;
            rightKneeX = v;
        }, this.oscP("knee_r:tx"), nil, port);

        knee_ry = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightKneeY = v - rightKneeY;
            rightKneeY = v;
        }, this.oscP("knee_r:ty"), nil, port);

        knee_rz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accRightKneeZ = v - rightKneeZ;
            rightKneeZ = v;
        }, this.oscP("knee_r:tz"), nil, port);

        dumps[\right_knee] = Routine{
            inf.do{
                format("PERSON" ++ personId + "RIGHT KNEE: x=%,y=%,z=%, accX=%,accY=%,accZ=%", rightKneeX, rightKneeY, rightKneeZ, accRightKneeX, accRightKneeY, accRightKneeZ).postln;
                dump_timeout.wait;
            }
        };

        // LEFT KNEE
        knee_lx = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftKneeX = v - leftKneeX;
            leftKneeX = v;
        }, this.oscP("knee_l:tx"), nil, port);

        knee_ly = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftKneeY = v - leftKneeY;
            leftKneeY = v;
        }, this.oscP("knee_l:ty"), nil, port);

        knee_lz = OSCFunc({ |msg|
            var v = msg[1..].mean;
            accLeftKneeZ = v - leftKneeZ;
            leftKneeZ = v;
        }, this.oscP("knee_l:tz"), nil, port);

        dumps[\left_knee] = Routine{
            inf.do{
                format("PERSON" ++ personId + "LEFT KNEE: x=%,y=%,z=%, accX=%,accY=%,accZ=%", leftKneeX, leftKneeY, leftKneeZ, accLeftKneeX, accLeftKneeY, accLeftKneeZ).postln;
                dump_timeout.wait;
            }
        };
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
        ^[accHeadX, accHeadY, accHeadZ].abs.maxItem;
    }

    kneesAccAny {
        ^[accLeftKneeX, accLeftKneeY, accLeftKneeZ, accRightKneeX, accRightKneeY, accRightKneeZ];
    }

    spineAccAny {
        ^[accSpineX, accSpineY, accSpineZ].abs.maxItem;
    }

    accX {
        ^[accHeadX, accLeftHandX, accRightHandX, accLeftHandTipX, accRightHandTipX].maxItem;
    }

    accY {
        ^[accHeadY, accLeftHandY, accRightHandY, accLeftHandTipY, accRightHandTipY].maxItem;
    }

    dump {
        arg name, run = true;
        if(run, {
            dumps[name].reset;
            dumps[name].play;
        }, {
            dumps[name].stop;
        });
    }
}