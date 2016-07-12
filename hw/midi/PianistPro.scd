(
~d = (
    deviceName: "iRig BlueBoard",
    protocol: 'midi',
    deviceType: 'controller',
    elementTypes: [\button, \slider],
    status: (
        linux: "unknown",
        osx: "unknown",
        win: "unknown"),

    idInfo: "iRig BlueBoard",

    //hardwarePages: [1, 2, 3, 4],

    deviceInfo: (
        // vendorURI: 'http://company.com/products/this',
        // manualURI: 'http://company.com/products/this/manual.pdf',
        // description: ,
        // features: [],
        // notes: ,
        // hasScribble: false
    ),
    elementsDesc: (
        elements: [
            (
                key: \bt,
                shared: ('midiMsgType': 'cc', 'midiChan': 0, 'spec': 'midiCC'),
                elements: [
                    // ------ cc -------------
                    ( key: \A, 'midiNum':  1, 'elementType': 'button'),
                    ( key: \B, 'midiNum':  2, 'elementType': 'button'),
                    ( key: \C, 'midiNum':  3, 'elementType': 'button'),
                    ( key: \D, 'midiNum':  4, 'elementType': 'button'),
                    ( key: \A, 'midiNum':  5, 'elementType': 'button'),
                    ( key: \B, 'midiNum':  6, 'elementType': 'button'),
                    ( key: \C, 'midiNum':  7, 'elementType': 'slider'),
                    ( key: \D, 'midiNum':  8, 'elementType': 'button'),
                    ( key: \D, 'midiNum':  21, 'elementType': 'slider'),
                ]
            ),
            (
                key: 'prog',
                type: 'slider',
                midiMsgType: \program,
                midiChan: 0,
                spec: \midiProgram,
             )
        ]

    )
);
)



m = MKtl( \testMIDI, ~d );
m.rebuild( ~d ); // updating it
m.gui
m.elAt(\prog)
m.elAt(\bt, \D).action = { |m| m.value.postln };
m.elAt(\prog).action = { |m| m.deviceValue.postln };
m.elAt(\play, \off).action = { "OFFFFF".postln };