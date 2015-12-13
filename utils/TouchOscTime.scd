(
var w, g_title, g_btn_start, g_clock, g_select, g_btn_pause;
var osc_ip = "192.168.0.194";
var osc_port = 9000;
var seconds = 0;
var osc_path = "/1/label";

var sendOsc = {|value|
    var t = value.asTimeString[0..7];
    b.sendMsg(osc_path, t);
    b.sendMsg(osc_path, t);
    b.sendMsg(osc_path, t);
    {g_clock.string_(t)}.defer;

};

    b = NetAddr.new(osc_ip, osc_port);
    t = Task ({
        inf.do( {
        sendOsc.value(seconds);
        seconds = seconds + 1;
        1.wait;
        }
        )
});

w = Window.new("TouchOSC clock", Rect(200, 200, 400, 300)).alwaysOnTop_(true).background_(Color.gray).front;

g_clock = StaticText.new(w, Rect(0, 0, 400, 70)).font_(Font("Helvetica", 60)).stringColor_(Color()).string_("00:00:00");

g_title = GTitle(composer: "TouchOSC", piece: "Visual clock");
g_btn_start = Button().states_([["Start", Color.black, Color.green], ["Stop", Color.red, Color.gray]]);
g_btn_start.action_({ |btn|
    switch(btn.value,
        1, {t.start;},
        0, {
            seconds = 0;
            sendOsc.value(0);
            t.stop;
            g_btn_pause.value_(0);
            }
    );
});

g_btn_pause = Button().states_([["Pause", Color.black, Color.yellow], ["Pause", Color.black, Color.gray]]);
g_btn_pause.action = { |btn|
    switch(btn.value,
        1, {t.pause},
        0, {t.resume};
    );
};

g_select = PopUpMenu(w, Rect(10, 10, 180, 20));
g_select.items = ["1", "2", "3", "4", "5", "6", "7", "8"];
g_select.action = { |menu|
    osc_path = "/%/label".format(menu.value + 1);
    osc_path.postln;
};

w.layout_ (
    VLayout(g_title, g_clock, g_select, HLayout(g_btn_start, g_btn_pause)).spacing_(0).margins_(20);
);

w.onClose = {
    sendOsc.value(0);
    t.free;
};
)
