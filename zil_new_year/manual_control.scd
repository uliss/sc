// OSC control addr
n = NetAddr("127.0.0.1", 7000);

// {SinOsc.ar([440, 460])}.play

// k = NYSketches.new(NetAddr("localhost", 10001));
k = NYSketches.new(NetAddr("alex", 10000));
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
k.gadanie.reload; // NB!

k.bubbles.start
k.bubbles.change_bass
k.bubbles.release
k.bubbles.stop

k.stol.start_in
k.stol.stop
k.stol.start_out

k.seledka.reload; //NB!
k.seledka.play1;
k.seledka.play2;
k.seledka.stop;
k.seledka.play3;
k.seledka.stop;

k.dvoinik.start;
k.dvoinik.pause;
k.dvoinik.resume;
k.dvoinik.stop;

k.drazhe.start;
k.drazhe.freeze(true)
k.drazhe.freeze(false)
k.drazhe.release(5);
k.drazhe.stop;


k.metel.start(4)
k.metel.release(4);
k.metel.stop;

k.kuranty.start
k.kuranty.release(7)
k.kuranty.stop;



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
