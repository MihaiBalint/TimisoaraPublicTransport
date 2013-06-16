/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/
package ro.mihai.tpt.utils;

import java.util.NoSuchElementException;

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.Estimate;

public enum EstimateVehicleStatusEx {
	Away(Estimate.VehicleStatus.Away, R.string.lblVStatusAway),
	Boarding(Estimate.VehicleStatus.Boarding, R.string.lblVStatusBoarding),
	Arriving(Estimate.VehicleStatus.Arriving, R.string.lblVStatusArriving),
	Departing(Estimate.VehicleStatus.Departing, R.string.lblVStatusDeparting),
	ArrivingDeparting(Estimate.VehicleStatus.ArrivingDeparting, R.string.lblVStatusArrivingDeparting);
	
	public final Estimate.VehicleStatus original;
	public final int descriptionId;

	private EstimateVehicleStatusEx(Estimate.VehicleStatus original, int descriptionId) {
		this.original = original;
		this.descriptionId = descriptionId;
	}
	
	public static EstimateVehicleStatusEx getAndroidEx(Estimate.VehicleStatus kind) {
		for(EstimateVehicleStatusEx ex: values())
			if (ex.original == kind)
				return ex;
		throw new NoSuchElementException();
	}
	
	public static int getDescriptionId(Estimate.VehicleStatus status) {
		return getAndroidEx(status).descriptionId;
	}
	
}
