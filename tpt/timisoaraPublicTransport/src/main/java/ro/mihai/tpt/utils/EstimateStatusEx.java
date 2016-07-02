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

public enum EstimateStatusEx {
	AllIsGood(Estimate.Status.AllIsGood, R.string.lblStatusAllIsGood), 
	WaitingToUpdate(Estimate.Status.WaitingToUpdate, R.string.lblStatusWaitingUpdate),
	NetworkError(Estimate.Status.NetworkError, R.string.lblStatusNetworkError),
	UpdateCanceled(Estimate.Status.UpdateCanceled, R.string.lblStatusUpdateCanceled);

	public final Estimate.Status original;
	public final int descriptionId;
	
	private EstimateStatusEx(Estimate.Status original, int descriptionId) {
		this.original = original;
		this.descriptionId = descriptionId;
	}
	
	public static EstimateStatusEx getAndroidEx(Estimate.Status kind) {
		for(EstimateStatusEx ex: values())
			if (ex.original == kind)
				return ex;
		throw new NoSuchElementException();
	}
	
	public static int getDescriptionId(Estimate.Status status) {
		return getAndroidEx(status).descriptionId;
	}

}
