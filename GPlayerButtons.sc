GPlayerButtons : CompositeView {
    var btn_play, btn_stop, btn_pause;
    var b_rect, player_state = 0;
    var btn_play_action, btn_stop_action, btn_pause_action;
    const stop = 0, play = 1, pause = 2;
    var osc_client, osc_path, osc_port;

    *new {
        arg parent, bounds = Rect(0, 0, 400, 70), oscPath = "/gplayer", oscPort = 10000;
        ^super.new(parent, bounds).init(oscPath, oscPort);
    }

    init { |oscPath, oscPort|
        osc_path = oscPath;
        osc_port = oscPort;
        postf("PlayerButton listening OSC messages \"%\" on port: % \n", osc_path, osc_port);
        b_rect = super.bounds;
        osc_client = OSCFunc(this.handleOsc, osc_path, nil, osc_port);

        this.onClose = {
            this.setStopped;
            osc_client.free;
        }
    }

    handleOsc { |msg|
        {
            switch(msg,
                \stop, {this.setStopped},
                \play, {this.setPlaying},
                \pause, {this.setPaused}
            );
        }.defer;
    }

    enablePlay { |value|
        if(btn_play.notNil) {
            if(value,
                { btn_play.states_([["Play ▶", Color.black, Color.white]])},
                { btn_play.states_([["Play ▶", Color.black, Color.gray]])}
            );
        }
    }

    enableStop { |value|
        if(btn_stop.notNil) {
            if(value,
                { btn_stop.states_([["Stop", Color.black, Color.white]])},
                { btn_stop.states_([["Stop", Color.black, Color.gray]])}
            );
        }
    }

    enablePause { |value|
        if(btn_pause.notNil) {
            if(value,
                { btn_pause.states_([["Pause ❚❚", Color.black, Color.white]])},
                { btn_pause.states_([["Pause ❚❚", Color.black, Color.gray]]) }
            );
        }
    }

    setInitState {
       this.enablePlay(true);
        this.enableStop(false);
        this.enablePause(false);
    }

    setPlaying {
        this.enablePlay(false);
        this.enableStop(true);
        this.enablePause(true);

        if(player_state != play) {
            btn_play_action.value;
            player_state = play;
        }
    }

    setStopped {
        this.enablePlay(true);
        this.enableStop(false);
        this.enablePause(false);

        if(player_state != stop) {
            btn_stop_action.value;
            player_state = stop;
        }
    }

    setPaused {
        switch (player_state,
            play, {
                this.enablePlay(false);
                this.enableStop(true);
                this.enablePause(false);

                btn_pause_action.value(true);
                player_state = pause;
            },
            pause, {
                this.enablePlay(false);
                this.enableStop(true);
                this.enablePause(true);

                btn_pause_action.value(false);
                player_state = play;
            }
        );
    }

    playButton {
        arg action;
        btn_play_action = action;

        btn_play = Button.new(this, Rect(0, 0, 80, 20)).action_({this.setPlaying});
    }

    stopButton {
        arg action;
        btn_stop_action = action;

        btn_stop = Button.new(this, Rect(100, 0, 80, 20)).action_({this.setStopped});
    }

    pauseButton {
        arg action;
        btn_pause_action = action;

        btn_pause = Button.new(this, Rect(200, 0, 80, 20)).action_({this.setPaused});
    }
}