/**
 * BobEngine - 2D game engine for Android
 *
 * Copyright (C) 2014, 2015, 2016 Benjamin Blaszczak
 *
 * BobEngine is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * version 2.1 as published by the free software foundation.
 *
 * BobEngine is provided without warranty; without even the implied
 * warranty of merchantability or fitness for a particular
 * purpose. See the GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with BobEngine; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301 USA
 *
 */
package com.bobbyloujo.bobengine.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * An player used for playing sound effects and music. This class is a simplified
 * wrapper around Android's SoundPool.
 * <br/>
 * <br/>
 * For large music files, use Android's MediaPlayer object instead.
 * 
 * @author Ben
 *
 */
public class SoundPlayer {

	/* Constants */
	private final int DEF_MAX_STREAMS = 10;        // The default max number of sounds played at once.

	/* Data */
	private Context context;                       // The context from which to retrieve resources.
	private SoundPool pool;                        // The Android SoundPool for playing sounds.

	public SoundPlayer(Context context) {
		init(context);
	}

	/**
	 * Initialize this SoundPlayer with a context and a maximum number of
	 * simultaneous sounds of 10.<br />
	 * <br />
	 * Does not have to be used if the context is the same as the context passed
	 * when this SoundPool's constructor was called.
	 * 
	 * @param context
	 *            - The context from which to retrieve sound resources.
	 */
	public void init(Context context) {
		if (pool != null) { // Destroy the current SoundPool if there is one.
			pool.release();
			pool = null;
		}

		pool = new SoundPool(DEF_MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		if (pool == null) Log.e("BobEngine", "Failed to create SoundPool");

		this.context = context;
	}

	/**
	 * Initialize this SoundPlayer with a context and a maximum number of
	 * simultaneous sounds of maxStreams.<br />
	 * <br />
	 * 
	 * @param context
	 *            - The context from which to retrieve sound resources.
	 * @param maxStreams
	 *            - The maximum number of sounds played at once.
	 */
	public void init(Context context, int maxStreams) {
		if (pool != null) { // Destroy the current SoundPool if there is one.
			pool.release();
			pool = null;
		}

		pool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
		if (pool == null) Log.e("BobEngine", "Failed to create SoundPool");

		this.context = context;
	}

	/**
	 * Add a sound to this player so that it can be played. Be sure to store the
	 * return value because it is used to play the added sound.
	 * 
	 * @param soundResource
	 *            - The sound resource to add (eg. R.raw.(...))
	 * @return The sound ID for the newly added sound. Keep track of this!
	 */
	public int newSound(int soundResource) {
		return pool.load(context, soundResource, 1);
	}

	/**
	 * Play a sound. You must the sound first with newSound(resource) to get a
	 * sound ID.
	 * 
	 * @param soundID
	 *            - The sound ID from newSound()
	 * @return The stream ID. Keep track of this if you need to
	 *         pause/stop/manipulate this sound while it's playing.
	 */
	public int play(int soundID) {
		return pool.play(soundID, 1, 1, 1, 0, 1);
	}

	/**
	 * Play a sound at a specific volume.
	 * 
	 * @param soundID
	 *            - The sound ID from newSound()
	 * @param volume
	 *            - Volume of the sound, range 0.0 to 1.0
	 * @return The stream ID. Keep track of this if you need to
	 *         pause/stop/manipulate this sound while it's playing.
	 */
	public int play(int soundID, float volume) {
		return pool.play(soundID, volume, volume, 1, 0, 1);
	}

	/**
	 * Play sound with advanced options.
	 * 
	 * @param soundID
	 *            - The sound ID from newSound()
	 * @param leftVolume
	 *            - Volume from the left speaker, from 0.0 to 1.0
	 * @param rightVolume
	 *            - Volume from the right speaker, from 0.0 to 1.0
	 * @param loop
	 *            - Number of times to loop. 0 to play once, -1 to loop forever.
	 * @param priority
	 *            - Priority of this stream. 0 is the lowest.
	 * @param rate
	 *            - Speed of this sound, from 0.5 to 2.0. (1.0 is the normal speed)
	 * @return The stream ID. Keep track of this if you need to
	 *         pause/stop/manipulate this sound while it's playing.
	 */
	public int play(int soundID, float leftVolume, float rightVolume, int loop, int priority, float rate) {
		return pool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
	}

	/**
	 * Play a sound and loop it forever.
	 * 
	 * @param soundID
	 *            - the sound ID from newSound()
	 * @return The stream ID. Keep track of this if you need to
	 *         pause/stop/manipulate this sound while it's playing.
	 */
	public int loop(int soundID) {
		return pool.play(soundID, 1, 1, 0, -1, 1);
	}

	/**
	 * Play a sound and loop it a specific number of times.
	 * 
	 * @param soundID
	 *            - the sound ID from newSound()
	 * @param times
	 *            - number of times to loop. 0 to play once, -1 to loop forever.
	 * @return
	 */
	public int loop(int soundID, int times) {
		return pool.play(soundID, 1, 1, 0, times, 1);
	}

	/**
	 * Stop a sound that is playing.
	 * 
	 * @param streamID
	 *            - The stream ID returned from the function used to play the
	 *            sound.
	 */
	public void stop(int streamID) {
		pool.stop(streamID);
	}

	/**
	 * Pause a sound that is playing.
	 * 
	 * @param streamID
	 *            - The stream ID returned from the function used to play the
	 *            sound.
	 */
	public void pause(int streamID) {
		pool.pause(streamID);
	}

	/**
	 * Resume a sound that has been paused.
	 * 
	 * @param streamID
	 *            - The stream ID returned from the function used to play the
	 *            sound.
	 */
	public void resume(int streamID) {
		pool.resume(streamID);
	}

	/**
	 * Pause all the sounds that are playing.
	 */
	public void pauseAll() {
		pool.autoPause();
	}

	/**
	 * Resume playing sounds that were paused with pauseAll().
	 */
	public void resumeAll() {
		pool.autoResume();
	}

	/**
	 * Change the volume of a sound that is playing.
	 * 
	 * @param streamID - The stream ID returned from the function used to play the
	 *            sound.
	 * @param volume - The new volume, range from 0.0 to 1.0
	 */
	public void setVolume(int streamID, float volume) {
		pool.setVolume(streamID, volume, volume);
	}

	/**
	 * Change the volume of a sound that is playing with different volumes
	 * for each speaker.
	 * 
	 * @param streamID - The stream ID returned from the function used to play the
	 *            sound.
	 * @param leftVolume - The new left speaker volume, from 0.0 to 1.0
	 * @param rightVolume - The new right speaker volume, from 0.0 to 1.0
	 */
	public void setVolume(int streamID, float leftVolume, float rightVolume) {
		pool.setVolume(streamID, leftVolume, rightVolume);
	}

	/**
	 * Change the rate (speed) at which a sound is playing.
	 * 
	 * @param streamID - The stream ID returned from the function used to play the
	 *            sound.
	 * @param rate - The new rate, from 0.5 to 2.0. (1.0 is the normal rate)
	 */
	public void setRate(int streamID, float rate) {
		pool.setRate(streamID, rate);
	}
}
