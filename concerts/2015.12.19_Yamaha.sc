EastBroadway {
    var track_path, <buffer;

    *new {
        ^super.new.init;
    }

    init {
        track_path = "/Users/serj/work/music/sounds/Julia Wolfe East Broadway track.wav";
        buffer = Buffer.read(Server.default, track_path);
        ^this;
    }
}

ROAI_III {
    var track_path, metronome_path, <bufferT, <bufferM;

    *new {
        ^super.new.init;
    }

    init {
        track_path = "/Users/serj/work/music/sounds/roai iii/IV-V-I-II-III module.aif";
        metronome_path = "/Users/serj/work/music/sounds/roai iii/IV-V-I-II-III metronome.aif";
        bufferT = Buffer.read(Server.default, track_path);
        bufferM = Buffer.read(Server.default, metronome_path);
        ^this;
    }
}

DaoustPetite {
    var track_path, <buffer;

    *new {
        ^super.new.init;
    }

    init {
        track_path = "/Users/serj/work/music/sounds/Daoust_Petite_musique_sentimentale.aiff";
        buffer = Buffer.read(Server.default, track_path);
        ^this;
    }
}