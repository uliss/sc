// wslib 2011

// TODO : make NanoKTL compatible

NanoKONTROL2Gui {
	classvar <window, <tabbedView;
	classvar <allViews;
	classvar <>inPort = 0;
	classvar <>allScenesActive = true; // if controllers double in scenes, only react to current

	*new { |height = 120, initMIDI = true|
		if( window.isNil )
			{
			if( initMIDI ) { MIDIWindow(true) };
			inPort =  this.findPort ? inPort;
			allViews = this.makeWindow( height );
			};
		}

	*findPort {
		^MIDIClient.sources.detectIndex({ |item| item.device == "nanoKONTROL2" });
	}

	*buttons { |scene|
		if( scene.notNil )
			{ ^allViews[ scene ][18..]; }
			{ ^allViews.collect( _[18..] ) };
		}

	*sliders { |scene|
		if( scene.notNil )
			{ ^allViews[ scene ][..8]; }
			{ ^allViews.collect( _[..8] ) };
		}

	*knobs { |scene|
		if( scene.notNil )
			{ ^allViews[ scene ][9..17]; }
			{ ^allViews.collect( _[9..17] ) };
		}

	*setScene { |scene = 0|
		tabbedView.focus( scene );
		this.changed( \scene, scene );
	}

	*currentScene { ^tabbedView.activeTab }

	*isInScene { |scene| ^this.currentScene == scene }

	*makeWindow {	 |height = 120|
		window =  Window( "nanoKONTROL2", Rect(800, 80, 180+(8*54), height+8+16), false ).front;

        ^this.makeScene(height, 1, window);
		}

	*makeScene {	|height = 120, scene = 1, view|
		var sliders, knobs, buttons, controls;
        var btn_colors;
		var controlsContainer;
		var sliderContainers;
		var allViews, allControllers, usedChannels, usedControllers, resp;

		view = view ? { Window( "nanoKontrol2 :: scene %".format(scene),
			Rect(100, 100, 180+(8*54), height+8 ), false ).front; };
		view.addFlowLayout;
        view.background_( Color.gray(0.45).alpha_(1) );

		controlsContainer = CompositeView( view, 165@height );


		StaticText( controlsContainer, Rect( 0, 0, 120, height - 70) )
			.string_("KORG").align_( \center )
			.font_( Font( "Helvetica-Bold", 13 ) );

		controlsContainer = CompositeView( controlsContainer, Rect(0,height-65, 170, 50) )
			.background_( Color.gray(0.85).alpha_(0.75) );

		controlsContainer.addFlowLayout;

		controls = { |i|
			var state;
			state = [ 'rewind', 'forward', 'stop', 'play', 'record' ][i];

			RoundButton( controlsContainer, 28@19 ).radius_(4)
				.states_( [
				[ state, if( i != 4 ) { Color.black } { Color.red }, Color.white.alpha_(0.5) ],
				[ state, if( i != 4 ) { Color.black } { Color.red },
					Color.red.blend( Color.white, 0.25 ).alpha_(0.5) ]]
					);
			}!5;

		sliderContainers = {
			CompositeView( view, 50@height )
				.background_( Color.gray(0.85).alpha_(0.75) );
			 }!8;

        btn_colors = [Color.yellow(), Color.blue(), Color.red()];

		sliderContainers.do({ |ct, i|
			var knob, slider, button;
			StaticText( ct, Rect( 4, 4, 20, 20 ) ).string_( (i+1).asString );
			knob = Knob( ct, Rect( 50 - 28, 4, 24, 24 ) ).value_(0.5).centered_(true);
			slider = SmoothSlider( ct, Rect( 50 - 26, 32, 20, height - (4+32) ) );
			button = { |i|
				RoundButton( ct, [Rect( 4, 32, 15, 15 ),
                    Rect( 4, (height/2.0 + 8), 15, 15 ),
                    Rect( 4, height - (4+15), 15, 15 )][i])
					.states_([[ "", Color.clear, Color.white.alpha_(0.5)],
						[ 'record', btn_colors[i].alpha_(0.5),
							btn_colors[i].blend( Color.white, 0.25 ).alpha_(0.5) ]])
					.radius_(3)
				}!3;

			knobs = knobs.add( knob );
			sliders = sliders.add( slider );
			buttons = buttons.add( button );
			});

		allViews = sliders ++ knobs ++ buttons.flop[0] ++ buttons.flop[1] ++ buttons.flop[2] ++ controls;

		// factory preset scenes:
		allControllers = [[0], (0..71).select({ |item|
            ((8..15) ++ (24..31) ++ (40..47) ++ (56..63)).includes( item ).not }) ++ [43, 44, 42, 41, 45]].flop;

		allControllers.do({ |item|
			if( (usedChannels ? []).includes( item[0] ).not )
				{ usedChannels = usedChannels.add( item[0] ) };
			if( (usedControllers ? []).includes( item[1] ).not )
				{ usedControllers = usedControllers.add( item[1] ) };
			});

		resp = CCResponder( { |port, chan, cc, val|
			var view;
			if( allScenesActive or: { this.isInScene( scene-1 ) } ) {
                view =  allViews[ allControllers.detectIndex({ |item| item == [chan, cc] }) ];
				if( view.class == Knob )
					{ { view.valueAction = val/127; }.defer; }
					{ view.valueAction = val/127; };
			};
		}, inPort, usedChannels, usedControllers );

		view.onClose_({ resp.remove });

		^allViews;
		}

	}