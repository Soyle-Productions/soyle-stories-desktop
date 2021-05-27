package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.redescribeLocation.ReDescribeLocationController
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetails
import java.util.*

class LocationDetailsController(
  private val threadTransformer: ThreadTransformer,
  private val locationId: String,
  private val getLocationDetails: GetLocationDetails,
  private val getLocationDetailsOutputPort: GetLocationDetails.OutputPort,
  private val reDescribeLocationController: ReDescribeLocationController
) : LocationDetailsViewListener {

	override fun getValidState() {
		threadTransformer.async {
			getLocationDetails.invoke(UUID.fromString(locationId), getLocationDetailsOutputPort)
		}
	}

	override fun reDescribeLocation(newDescription: String) {
		reDescribeLocationController.reDescribeLocation(locationId, newDescription)
	}

}