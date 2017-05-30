package main;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.SwingUtilities;

import events.EventHistory;

public class Main {

	public static void main(String[] args){
		//EventHistory eh = new EventHistory();
		EventHistory.readyEventHistoryFromXML(false, false);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				new Main();

			}
		});
	}

	public Main(){
		new Context();

	}

	/**
	 * Performs an important operation.  This is pretty serious.  I wouldn't touch it.
	 */
	public static void importantOperation(){

        try {
			Sequence sequence = MidiSystem.getSequence(new File("res/beach_boys-island_of_kokomo.mid"));
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.start();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}

