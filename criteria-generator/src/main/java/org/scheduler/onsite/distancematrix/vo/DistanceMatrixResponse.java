package org.scheduler.onsite.distancematrix.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DistanceMatrixResponse {

	private ResourceSet[] resourceSets;

	public ResourceSet[] getResourceSets() {
		return resourceSets;
	}

	public void setResourceSets(ResourceSet[] resourceSets) {
		this.resourceSets = resourceSets;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResourceSet {

		private DistanceMatrixVO[] resources;

		public DistanceMatrixVO[] getResources() {
			return resources;
		}

		public void setResources(DistanceMatrixVO[] resources) {
			this.resources = resources;
		}
	}
}
