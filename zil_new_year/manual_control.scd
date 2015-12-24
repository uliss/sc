// OSC control addr
n = NetAddr("127.0.0.1", 7000);

// {SinOsc.ar([440, 460])}.play

// k = NYSketches.new(NetAddr("localhost", 10001));
k = NYSketches.new(NetAddr("alex", 10000));
k.gadanie.debug = true;
k.gadanie.debug = false;

//  гаснет свет
k.gadanie.start;
k.gadanie.stop;
k.gadanie.play_synth1;
k.gadanie.stop_synth1;
k.gadanie.play_synth2;
k.gadanie.stop_synth2;
k.gadanie.calibrate;
k.gadanie.person1.dump(\head);
k.gadanie.reload; // NB!

// появляется синяя заливка
k.bubbles.start
k.bubbles.change_bass
k.bubbles.release
k.bubbles.stop
// сделать дзинь!!! 3 раза

k.stol.start_in
k.stol.stop
k.stol.start_out

k.seledka.reload; //NB!
k.seledka.play1;
k.seledka.play2;
k.seledka.stop;
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


k.metel.amp(0.5);
k.metel.start(4)
k.metel.release(4);
k.metel.stop;

k.mixer.start;
k.mixer.play1;
k.mixer.play2;
k.mixer.stop;
k.mixer.release(0.3);

k.kuranty.start
k.kuranty.release(7)
k.kuranty.stop;

