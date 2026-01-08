variable "deployment_name" {
  default="liferay-self-hosted"
}
variable "region" {
  type=string
}
variable "prometheus_namespace" {
  default="monitoring"
}