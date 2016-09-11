TestGuidoPieceApp : GuidoTest {
    beforeEach {
        super.beforeEach;
        NodeJS_Widget.idx_count = 1;
    }

    test_new {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        this.expect(p).to.be.a_(GuidoPieceApp);
        this.expect(p.title).to.be.equal_("Partita");
        this.expect(p.composer).to.be.equal_("J.S.Bach");
        this.expect(p.oscPath).to.be.equal_("/partita");
        this.expect(p.httpPath).to.be.equal_("/piece");
        this.expect(p).to.be.identical_(GuidoPieceApp.new("Partita", "J.S.Bach", "/partita"));
        this.expect(p.phonesChannel).to.be.equal_(4);
        this.expect(p.isPlaying).to.be.false_;
        this.expect(p.isPaused).to.be.false_;
        this.expect(p.isStopped).to.be.true_;
        this.expect(p).listen.osc_("/partita");
        this.expect(p).listen.osc_("/guido/sync/piece");
        p.addTask(1, {});
        p.free;
        this.expect(p).not.listen.osc_("/partita");
        this.expect(p).not.listen.osc_("/guido/sync/piece");
        this.expect(p.taskRunner.asList).to.be.empty_;
    }

    test_patches {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        this.expect(p.addPatch()).to.be.nil_;
        this.expect(p.addPatch(\test)).to.be.nil_;
        this.expect(p.addPatch(\test, [])).to.be.nil_;
        this.expect(p.addPatch(\test, ["utils.tone"])).to.be.not.nil_;
        this.expect(p.patch(\test)).to.be.not.nil_;
        this.expect(p.patch("test")).to.be.not.nil_;
        this.expect(p.patch(\test)).to.be.a_(Patch);
        p.removePatch(\test);
        this.expect(p.patch(\test)).to.be.nil_;
        p.free
    }

    test_params {
        var p, dict;
        p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addPatch(\test, ["utils.tone"]);
        this.expect(p.params[\test]).keys.containsAny_(\amp, \freq);

        {
            var opts;
            opts = p.params;

            this.expect(p.saveParams).to.be.equal_(opts);
            p.set(\test, \amp, 1);
            this.expect(p.params).to.be.not.equal_(opts);
            this.expect(opts).to.be.equal_(p.loadParamsDict);
            p.loadParams;
            this.expect(p.params).to.be.equal_(opts);
        }.value;
        p.free
    }

    test_widgets {
        var listeners = this.oscListeners;
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addWidget(\knob1, NodeJS_Knob.new);
        this.expect(p.widget("knob1")).to.be.not.nil_;
        p.showWidgets;
        this.expect(p).to.sendOSC_(
            "/guido/forward",
            "/guido/widget/add",
            "{\"max\": 1,\"size\": 100,\"min\": 0,\"parent\": \"ui-elements\",\"oscPath\": \"/ui\",\"value\": 0,\"idx\": \"knob1\",\"label\": \"\",\"type\": \"knob\"}");

        this.expect(p).listen.osc_("/guido/ui/knob1");

        p.hideWidgets;
        this.expect(p).listen.osc_("/guido/ui/knob1");
        this.expect(p).to.sendOSC_("/guido/forward", "/guido/widget/remove", "knob1");

        p.removeWidget("knob1");
        this.expect(p).not.listen.osc_("/guido/ui/knob1");
        this.expect(p).to.sendOSC_("/guido/forward", "/guido/widget/remove", "knob1");
        this.expect(p.widget("knob1")).to.be.nil_;
        p.free;

        this.expect(listeners).to.be.equal_(this.oscListeners);
    }

    test_binding {
        var tmp;
        var listeners = this.oscListeners;
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addWidget(\knob1, NodeJS_Knob.new);
        p.addPatch(\tone, ["utils.tone"]);
        p.set(\tone, \amp, 0.7);
        p.bindW2P(\knob1, \tone, \amp);
        this.expect(p.hasWidgetBinding(\knob1)).to.be.true_;
        this.expect(p.hasPatchBinding(\tone, \amp)).to.be.true_;
        this.expect(p.findBindedPatch(\knob1)).to.be.equal_(("tone" -> "amp"));
        this.expect(p.findBindedWidget(\tone, \amp)).to.be.equal_(\knob1);

        this.expect(p.widget(\knob1).value).to.be.equal_(0.7);

        p.removePatchBinding(\tone, \freq);
        this.expect(p.hasWidgetBinding(\knob1)).to.be.true_;
        p.removePatchBinding(\tone, \amp);
        this.expect(p.hasWidgetBinding(\knob1)).to.be.false_;
        p.bindW2P(\knob1, \tone, \amp);
        p.addWidget(\knob2, NodeJS_Knob.new(440, 20, 20000));
        p.bindW2P(\knob2, \tone, \freq);

        this.expect(p.hasWidgetBinding(\knob1)).to.be.true_;
        this.expect(p.hasWidgetBinding(\knob2)).to.be.true_;

        p.removeAllPatchBindings(\tone);
        this.expect(p.hasWidgetBinding(\knob1)).to.be.false_;
        this.expect(p.hasWidgetBinding(\knob2)).to.be.false_;

        p.bindW2P(\knob1, \tone, \amp);
        p.bindW2P(\knob2, \tone, \freq);

        this.expect(p.hasWidgetBinding(\knob1)).to.be.true_;
        this.expect(p.hasWidgetBinding(\knob2)).to.be.true_;

        this.expect(p.findBindedPatch(\knob2).key).to.be.equal_("tone");
        p.removeWidgetBinding(\knob2);
        this.expect(p.hasWidgetBinding(\knob1)).to.be.true_;
        this.expect(p.hasWidgetBinding(\knob2)).to.be.false_;

        p.removeWidget(\knob1);
        this.expect(p.hasWidgetBinding(\knob1)).to.be.false_;

        p.free;
        this.expect(listeners).to.be.equal_(this.oscListeners);
    }

    test_set {
        var listeners = this.oscListeners;
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addWidget(\knob1, NodeJS_Knob.new);
        p.addPatch(\tone, ["utils.tone"]);
        p.bindW2P(\knob1, \tone, \amp);

        p.set(\tone, \amp, 0.7);
        this.expect(p.widget(\knob1).value).to.be.equal_(0.7);
        this.expect(p.patch(\tone).argFromName(\amp).value).to.be.equal_(0.7);

        p.free;
        this.expect(listeners).to.be.equal_(this.oscListeners);
    }

    test_task {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addTask(20, {});
        this.expect(p.hasTask("00:20")).to.be.true_;
        this.expect(p.hasTask("00:21")).to.be.false_;
        p.addTask("01:20", {});
        this.expect(p.hasTask(80)).to.be.true_;

        {
            var a, b;
            p.addTask("01:25", { a = 1});
            p.addTask(85, { |tm| b = tm }, \default, 45);
            p.currentTime = "01:21";
            p.taskRunner.runTasks;

            this.expect(a).to.be.not.equal_(1);
            this.expect(b).to.be.not.equal_(2);

            p.currentTime = "01:25";
            p.taskRunner.runTasks;

            this.expect(a).to.be.equal_(1);
            this.expect(b).to.be.equal_(45);

        }.value;

        this.expect(p.hasTask(85)).to.be.true_;
        p.removeTask(85);
        this.expect(p.hasTask(85)).to.be.false_;

        p.addTask("01:20", {});
        p.removeAllTasks;
        this.expect(p.hasTask(80)).to.be.false_;

        p.free;
    }

    test_osc {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        this.receiveOSC("/partita", "play");
        this.expect(p.isPlaying).to.be.true_;
        this.receiveOSC("/partita", "pause");
        this.expect(p.isPaused).to.be.true_;
        p.currentTime = 120;
        this.receiveOSC("/partita", "stop");
        this.expect(p.isStopped).to.be.true_;
        this.expect(p.currentTime).to.be.equal_(0);
        p.free;
    }

    test_monitor {
        var listeners = this.oscListeners;
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        p.addMonitorWidget(false);

        p.free;
        this.expect(listeners).to.be.equal_(this.oscListeners);
    }

    test_load_tasks {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        var fname = p.tasksFilename;
        this.expect(p.loadTasks()).to.be.nil_;

        {
            var f = File.new(fname, "w");
            f << "0:01 print 1\n";
            f << "1:01 unknown 1\n";
            f.close;
        }.value;

        this.expect(p.loadTasks()).to.be.not.nil_;
        this.expect(p.hasTask(1)).to.be.true_;
        this.expect(p.hasTask(61)).to.be.false_;

        File.delete(fname);

        p.free;
    }

    test_save_tasks {
        var p = GuidoPieceApp.new("Partita", "J.S.Bach", "/partita");
        var fname = p.tasksFilename;
        p.addTask("1:20", {}, \print, 2);
        this.expect(p.hasTask(80)).to.be.true_;
        p.saveTasks();
        this.expect(fname).exists_;

        {
            var f = File.new(fname, "r");
            this.expect(f.readAllString).to.be.equal_("00:01:20 print 2\n");
            f.close;
        }.value;

        File.delete(fname);
        p.free;
    }
}

// TestGuidoPieceApp.run
