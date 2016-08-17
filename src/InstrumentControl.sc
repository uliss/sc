SP_InstrumentControl : SP_AbstractOscControl {
    var players;

    *new {
        arg oscPath = "/sc/utils/instr";
        ^super.new(oscPath).initManager;
    }

    initManager {
        players = Dictionary.new;
    }

    processOsc {
        arg msg;
        var instr_name = msg[1].asString;
        var action = msg[2].asString;
        var player = players[instr_name];

        if(player.isNil) { // add new elements
            var instr = Instr(instr_name);
            if(instr.isNil) {
                "[%] invalid instrument name: %".format(this.class, instr_name).warn;
                ^nil;
            };

            players[instr_name] = SP_InstrumentPlayer.new(instr);
            player = players[instr_name];
        };

        switch(action,
            "init", { player.initPlayer(instr_name, *msg[3..]) },
            "play", { player.play(*msg[3..]) },
            "stop", { player.stop },
            "release", { player.release },
            "set", {  player.set(*msg[3..]) },
            "igui", {  player.instrumentGui },
            "pgui", {  player.playerGui },
            { "[%] unknown message format: %".format(this.class, msg).postln }
        )
    }

    player { |name| ^players[name] }

    playerNames { ^players.keys }

    stopAll {
        players.do { |p| p.stop }
    }

    releaseAll {
        players.do { |p| p.release }
    }
}

