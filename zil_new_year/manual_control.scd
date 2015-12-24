// OSC control addr
n = NetAddr("127.0.0.1", 7000);

k = NYSketches.new;
k.gadanie.debug = true;
k.gadanie.debug = false;
k.gadanie.start;
k.gadanie.stop;
k.gadanie.play_synth1;
k.gadanie.stop_synth1;
k.gadanie.play_synth2;
k.gadanie.stop_synth2;
k.gadanie.calibrate;
k.gadanie.person1.dump(\head);
k.gadanie.reload;

k.bubbles.start
k.bubbles.change_bass
k.bubbles.release
k.bubbles.stop

k.stol.start_in
k.stol.stop
k.stol.start_out


/////////////////////////////////
/// DVOINIK  MANUAL CONTROL
/////////////////////////////////
d = DvoinikScene.new([\delay, 2]);
d.start;
d.pause;
d.resume;
d.stop;
d.release(4);
d.amp(2);
// osc
n.sendMsg("/dvoinik", \start);
n.sendMsg("/dvoinik", \pause);
n.sendMsg("/dvoinik", \resume);
n.sendMsg("/dvoinik", \stop);
n.sendMsg("/dvoinik", \release, 2);
n.sendMsg("/dvoinik", \amp, 2);


/////////////////////////////////
// DRAZHE MANUAL CONTROL
/////////////////////////////////
n.sendMsg("/drazhe", \start);
n.sendMsg("/drazhe", \stop);
n.sendMsg("/drazhe", \freeze, 1);
n.sendMsg("/drazhe", \freeze, 0);

~scenes.drazheParam[\timeout] = 0.15;
~scenes.drazheParam[\acc_threshold_up] = 0.21; // 0.11
~scenes.drazheParam[\acc_threshold_down] = 0.1; // 0.7


/// KURANTY
n.sendMsg("/kuranty", \start);
n.sendMsg("/kuranty", \stop);
n.sendMsg("/kuranty", \release, 2);

/// WIND
~scenes.init;
w = WindScene.new([\amp, 0.1], ~scenes.person1, ~scenes.person2);
w.debug = true;
w.start
w.start(\amp, 2);
w.stop
w.pause
w.resume
w.reset
w.amp(2)
w.amp(1)
w.release(3)
w.synthSet(\amp, 2);


n.sendMsg("/wind", \start, \amp, 1);
n.sendMsg("/wind", \start, \amp, 2);
n.sendMsg("/wind", \stop);
n.sendMsg("/wind", \release, 4);
n.sendMsg("/wind", \amp, 0.4);
n.sendMsg("/wind", "param", "amp", 1.01);
n.sendMsg("/wind", "param", "rate1", 2);
n.sendMsg("/wind", "param", "rate2", 1.1);

//////////////////////////////////
////  SELEDKA MANUAL CONTROL  ////
//////////////////////////////////
~scenes.init;
t = SeledkaScene.new(kinectPerson1: ~scenes.person1, kinectPerson2: ~scenes.person2);
t.play1;
t.play2;
t.beat_metal1;
t.beat_metal2;
t.beat_onion1;
t.beat_onion2;
t.beat_microwave;
t.beat_bass;
t.play3;
t.stop;
// SELEDKA OSC
n.sendMsg("/seledka", \start);
n.sendMsg("/seledka", \start, 1);
n.sendMsg("/seledka", \start, 2);
n.sendMsg("/seledka", \start, 3);
n.sendMsg("/seledka", \stop);

/////////////////////////////////
/// FADER  MANUAL CONTROL
/////////////////////////////////
~scenes.init;
n.sendMsg("/xfader", \start);
n.sendMsg("/xfader", \stop);
n.sendMsg("/xfader", \release, 2);
n.sendMsg("/xfader", \xfade, -1);
n.sendMsg("/xfader", \xfade, -0.9);
n.sendMsg("/xfader", \xfade, 0);
n.sendMsg("/xfader", \xfade, 0.7);
n.sendMsg("/xfader", \amp, 0.1);
n.sendMsg("/xfader", \amp, 1);
