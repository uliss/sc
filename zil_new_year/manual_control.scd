// OSC control addr
n = NetAddr("127.0.0.1", 7000);
n = NetAddr("alex", 10000);
n.sendMsg("/freeze", 0);

// {SinOsc.ar([440, 460])}.play

// k = NYSketches.new(NetAddr("localhost", 10001));
k = NYSketches.new(NetAddr("alex", 10000));

k.gadanie.debug = true;
k.gadanie.debug = false;
k.dumpKinect(true);
k.dumpKinect(false);

//  гаснет свет
k.gadanie.start;
k.gadanie.stop; // может быть позже?
k.gadanie.play_synth1;
k.gadanie.stop_synth1;
k.gadanie.play_synth2;
k.gadanie.stop_synth2;
k.gadanie.calibrate;
k.gadanie.person1.dump(\head);
k.gadanie.reload; // NB!

// появляется синяя заливка
k.bubbles.start
// k.gadanie.stop; ?????
k.bubbles.change_bass
k.bubbles.release
// k.bubbles.stop

k.stol.start_in
k.stol.stop
// k.stol.start_out

k.seledka.reload; //NB!
k.seledka.play1;
// выход девушек три раза
k.seledka.play2;

k.seledka.play3;


// уносим стол
k.seledka.stop;

// я включаю
k.dvoinik.start;
// включтьб фонарь в 1:15
// выключить фонарь когда вынесли елку
k.dvoinik.pause;
k.dvoinik.resume;
k.dvoinik.stop;

k.drazhe.start;
k.drazhe.freeze(true)
k.drazhe.freeze(false)
k.drazhe.release(5);
k.drazhe.stop;


k.metel.start

k.mixer.play1;
k.mixer.play2;
k.mixer.stop;

k.metel.stop;
k.metel.release(4);


k.mixer.release(0.3);

// выносить елку
k.metel.release(4);
k.kuranty.start
k.kuranty.release(7)
k.kuranty.stop;

