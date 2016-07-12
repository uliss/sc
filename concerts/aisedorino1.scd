o = s.options;
o.device = "Saffire";

{SinOsc.ar(440, 0, 0.1)}.play;

(
var  accX, accY, accZ, synth1, gyroPitch, gyroRoll, gyroYaw;
b = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");

~accX = 0;

~x = Bus.control(s, 1);
~y = Bus.control(s, 1);
~z = Bus.control(s, 1);

~gyroPitch = Bus.control(s, 1);
~gyroRoll = Bus.control(s, 1);
~gyroYaw = Bus.control(s, 1);

// при резком движении смена источника звука

SynthDef(\synth1, {
    arg x, y, z, pitch, roll, yaw;
    var carfreq, modfreq, modidx, aout, aout2, amp, pan;

    carfreq = x.linlin(0, 1, 100, 6000).lag(1);
    modfreq = y.linlin(0, 1, 10, 1000).lag(0.2);
    modidx = z.linlin(0, 1, 2, 10).lag(0.1);


    amp = pitch.linlin(0, 1, 0, 0.5).lag(2);
    amp = Select.kr(pitch < 0.1, [amp, 0]);

    // pan = roll.linlin(0, 1, 0.2, 0.8).lag(4);
    pan = 0.5;

    aout = PMOsc.ar(carfreq, modfreq, modidx);
    aout2 = Pan2.ar(aout, pan).clip(-1, 1);
    Out.ar(0, aout2 * amp * 0.3);
}).add;

SynthDef(\synth2, {
    arg x, y, z, pitch, roll, yaw;
    var freq, amp, aout, aout2;

    freq = yaw.linexp(0, 1, 40, 8000).lag(2.01);
    amp = pitch.linlin(0, 1, 0, 1).lag(2);
    amp = Select.kr(pitch < 0.05, [amp, 0]);

    aout = Pulse.ar(freq);
    aout2 = Pan2.ar(aout, roll).clip(-1, 1);
    Out.ar(0, aout * amp * 0.3);
}).add;

SynthDef(\synth3, {
    arg x, y, z, pitch, roll, yaw;
    var freq, amp, aout, aout2;

    freq = x.linexp(0, 1, 40, 9000).lag(2.01);
    amp = pitch.linlin(0, 1, 0, 1).lag(2);
    amp = Select.kr(pitch < 0.05, [amp, 0]);

    aout = GbmanL.ar(freq);
    aout2 = Pan2.ar(aout, yaw).clip(-1, 1);
    Out.ar(0, aout * amp * 0.15);
}).add;

SynthDef(\synth4, {
    arg x, y, z, pitch, roll, yaw;
    var freq, amp, aout, aout2;

    freq = y.linexp(0, 1, 1, 200).lag(1);

    aout = Dust2.ar(freq);
    aout2 = Pan2.ar(aout, 0.5).clip(-1, 1);
    Out.ar(0, aout * 0.6);
}).add;

SynthDef(\synth5, {
    arg x, y, z, pitch, roll, yaw;
    var freq, amp, aout, aout2;

    freq = roll.linexp(0, 1, 190, 20010).lag(1);
    amp = pitch.linlin(0, 1, 0, 1).lag(2);
    amp = Select.kr(pitch < 0.2, [amp, 0]);

    aout = StandardL.ar(freq);
    aout2 = Pan2.ar(aout, yaw).clip(-1, 1);
    Out.ar(0, aout * amp * 0.1);
}).add;

SynthDef(\synth6, {
    arg x, y, z, pitch, roll, yaw;
    var freq, amp, aout, aout2;
    var trate, dur, rate;

    amp = pitch.linlin(0, 1, 0, 1).lag(2);
    amp = Select.kr(pitch < 0.2, [amp, 0]);

    trate = roll.linlin(0, 1, 2,200);
    dur = 4 / trate;
    rate = Dseq([10, 1, 1, 0.5, 0.5, 0.2, 0.1], inf);
    aout = TGrains.ar(2, Impulse.ar(trate), b, rate, y.linlin(0, 1, 0,BufDur.kr(b)), dur, Dseq([-1, 1], inf), 0.1, 2);

    aout2 = Pan2.ar(aout, yaw).clip(-1, 1);
    Out.ar(0, aout * amp * 0.1);
}).add;


synth1 = Synth(\synth4);
synth1.map(0, ~x);
synth1.map(1, ~y);
synth1.map(2, ~z);
synth1.map(3, ~gyroPitch);
synth1.map(4, ~gyroRoll);
synth1.map(5, ~gyroYaw);

OSCdef(
    \ipad1, {|msg, time, addr, recvPort|
   {
            gyroPitch = msg[4].asFloat.linlin(0, 100, 0, 1);
            gyroRoll = msg[5].asFloat.linlin(0, 100, 0, 1);
            gyroYaw = msg[6].asFloat.linlin(0, 100, 0, 1);
            ~gyroPitch.value = gyroPitch;
            ~gyroRoll.value = gyroRoll;
            ~gyroYaw.value = gyroYaw;
   }.defer;
 }, '/gyro', nil, 10000);

OSCdef(
    \ipad_acc, {|msg, time, addr, recvPort|
   {
           accX = msg[1].asFloat.linlin(0, 100, 0, 1);
            accY = msg[2].asFloat.linlin(0, 100, 0, 1);
            accZ = msg[3].asFloat.linlin(0, 100, 0, 1);
            ~x.value = accX;
            ~y.value = accY;
            ~z.value = accZ;
   }.defer;
}, '/accelerometer', nil, 10000);


)

OSCdef(\ipad_acc).clear;