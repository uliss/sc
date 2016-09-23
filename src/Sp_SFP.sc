+ SFP {
    start_ { arg v;
        firstBeatIsAt = v;
    }

    start { ^firstBeatIsAt }

    loadBuffersToBundle { arg bundle;
        var begin = this.start ? 0;
        // prepare all the preloads
        segmentBuffers = List.new;
        this.preloadData(this.start, nil, group, bundle, segmentBuffers);
    }
}