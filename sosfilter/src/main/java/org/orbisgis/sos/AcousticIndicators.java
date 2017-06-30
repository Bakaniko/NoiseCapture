/*
 * This file is part of the NoiseCapture application and OnoMap system.
 *
 * The 'OnoMaP' system is led by Lab-STICC and Ifsttar and generates noise maps via
 * citizen-contributed noise data.
 *
 * This application is co-funded by the ENERGIC-OD Project (European Network for
 * Redistributing Geospatial Information to user Communities - Open Data). ENERGIC-OD
 * (http://www.energic-od.eu/) is partially funded under the ICT Policy Support Programme (ICT
 * PSP) as part of the Competitiveness and Innovation Framework Programme by the European
 * Community. The application work is also supported by the French geographic portal GEOPAL of the
 * Pays de la Loire region (http://www.geopal.org).
 *
 * Copyright (C) IFSTTAR - LAE and Lab-STICC – CNRS UMR 6285 Equipe DECIDE Vannes
 *
 * NoiseCapture is a free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of
 * the License, or(at your option) any later version. NoiseCapture is distributed in the hope that
 * it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation,Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301  USA or see For more information,  write to Ifsttar,
 * 14-20 Boulevard Newton Cite Descartes, Champs sur Marne F-77447 Marne la Vallee Cedex 2 FRANCE
 *  or write to scientific.computing@ifsttar.fr
 */

package org.orbisgis.sos;

import java.util.List;

/**
 * Created by G. Guillaume on 18/06/15.
 * Calculation of some acoustic indicators
 */
public class AcousticIndicators {

    // Time periods (in s) for the calculations of the Slow (1 s) and Fast (0.125 s) equivalent sound pressure levels
    public static final double TIMEPERIOD_SLOW = 1.0;
    public static final double TIMEPERIOD_FAST = 0.125;

    // Reference sound pressure level [Pa]
    public static final double REF_SOUND_PRESSURE = 0.00002;

    /**
     * Calculation of the equivalent sound pressure level
     * @param inputSignal time signal [Pa]
     * @return equivalent sound pressure level [dB] not normalised by reference pressure.
     */
    public static double getLeq(double[] inputSignal, double refSoundPressure) {
        double sqrRms = 0.0;
        final double sqrRefSoundPressure = refSoundPressure * refSoundPressure;
        for (int idT = 1; idT < inputSignal.length; idT++) {
            sqrRms += inputSignal[idT] * inputSignal[idT];
        }
        return 10 * Math.log10(sqrRms / (inputSignal.length * sqrRefSoundPressure));
    }

    /**
     * Calculation of the equivalent sound pressure levels over a time period
     * @param inputSignal time signal [Pa]
     * @param sampleRate sampling frequency [Hz]
     * @param timePeriod time period (s)
     * @return double array of equivalent sound pressure levels [dB]
     */
    public static double[] getLeqT(double[] inputSignal, int sampleRate, double timePeriod, double refSoundPressure) {
        int subSamplesLength = (int)(timePeriod * sampleRate);      // Sub-samples length
        int nbSubSamples = inputSignal.length / subSamplesLength;
        double[] leqT = new double[nbSubSamples];
        int idStartForSub = 0;
        for (int idSub = 0; idSub < nbSubSamples; idSub++) {
            double[] subSample = new double[subSamplesLength];
            System.arraycopy(inputSignal, idStartForSub, subSample, 0, subSamplesLength);
            leqT[idSub] = getLeq(subSample, refSoundPressure);
            idStartForSub += subSamplesLength;
        }
        return leqT;
    }

    /**
     * Apply a Hann window to a signal
     * @param signal time signal
     * @return the windowed signal
     */
    public static double[] hannWindow(double[] signal) {

        // Iterate until the last line of the data buffer
        for (int n = 1; n < signal.length; n++) {
            // reduce unnecessarily performed frequency part of each and every frequency
            signal[n] *= 0.5 * (1 - Math.cos((2 * Math.PI * n) / (signal.length - 1)));
        }
        // Return modified buffer
        return signal;
    }

    /**
     * Apply a Hanning window to a signal
     * @param signal time signal
     * @return the windowed signal
     */
    public static float[] hannWindow(float[] signal) {

        // Iterate until the last line of the data buffer
        for (int n = 1; n < signal.length; n++) {
            // reduce unnecessarily performed frequency part of each and every frequency
            signal[n] *= 0.5 * (1 - Math.cos((2 * Math.PI * n) / (signal.length - 1)));
        }
        // Return modified buffer
        return signal;
    }


    public final static class SplStatistics {
        public final double min;
        public final double max;
        public final double mean;

        public SplStatistics(double min, double max, double mean) {
            this.min = min;
            this.max = max;
            this.mean = mean;
        }
    }
}