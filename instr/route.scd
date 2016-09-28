// output channels
Instr("route.->0|", { |in| Out.ar(0, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->1|", { |in| Out.ar(1, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->2|", { |in| Out.ar(2, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->3|", { |in| Out.ar(3, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->4|", { |in| Out.ar(4, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->5|", { |in| Out.ar(5, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->6|", { |in| Out.ar(6, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->7|", { |in| Out.ar(7, in) },[ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.->left|", { |in| Out.ar(0, in) },[ AudioSpec(1) ], outSpec: AudioSpec(1));
Instr("route.->right|", { |in| Out.ar(1, in) },[ AudioSpec(1) ], outSpec: AudioSpec(1));
Instr("route.->none|", { |in| Silence.ar },[ AudioSpec() ], outSpec: ControlSpec());
Instr("route.->N|", { |in, out|
    Out.ar(out, in)
},[ AudioSpec(1),  InOutBusSpec() ], outSpec: AudioSpec(1));

// take channels
Instr("route.@0", { |in| in.at(0) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@1", { |in| in.at(1) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@2", { |in| in.at(2) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@3", { |in| in.at(3) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@4", { |in| in.at(4) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@5", { |in| in.at(5) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@6", { |in| in.at(6) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@7", { |in| in.at(7) }, [ AudioSpec() ], outSpec: AudioSpec(1));
Instr("route.@N", { |in, at = 0| in.at(at) }, [
    AudioSpec(),
    InOutBusSpec(),
], outSpec: AudioSpec(1));

// input channels
Instr("route.0->", { In.ar(0) }, outSpec: AudioSpec(1));
Instr("route.1->", { In.ar(1) }, outSpec: AudioSpec(1));
Instr("route.2->", { In.ar(2) }, outSpec: AudioSpec(1));
Instr("route.3->", { In.ar(3) }, outSpec: AudioSpec(1));
Instr("route.4->", { In.ar(4) }, outSpec: AudioSpec(1));
Instr("route.5->", { In.ar(5) }, outSpec: AudioSpec(1));
Instr("route.6->", { In.ar(6) }, outSpec: AudioSpec(1));
Instr("route.7->", { In.ar(7) }, outSpec: AudioSpec(1));
Instr("route.N->", { |bus| In.ar(bus) }, [ InOutBusSpec() ], outSpec: AudioSpec(1));

// swap stereo channels
Instr("route.swap", { |in| [in.at(1), in.at(0)] }, [ AudioSpec(2) ], outSpec: AudioSpec(2));
Instr("route.pass", { |in| in }, [ AudioSpec() ], outSpec: AudioSpec());

Instr("route.split", { |in, split_bus = 2| Out.ar(split_bus, in); in; }, [AudioSpec(0), InOutBusSpec(2)], AudioSpec());

// headphones monitoring
Instr("route.->phones->", { |in, phones_ch = 4, phones_amp = 1|
    Out.ar(phones_ch, in * phones_amp);
    in
},[
    AudioSpec(),
    InOutBusSpec(4),
    [0, 10, \amp, 1, "amp"]
], outSpec: AudioSpec(1));

Instr("route.->phones|", { |in, phones_ch = 4, phones_amp = 1|
    Out.ar(phones_ch, in * phones_amp);
},[
    AudioSpec(),
    InOutBusSpec(4),
    [0, 10, \amp, 1, "amp"]
], outSpec: AudioSpec(1));
