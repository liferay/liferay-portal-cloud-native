resource "helm_release" "prometheus_collector" {
  create_namespace = true
  name             = "prometheus"
  repository       = "https://prometheus-community.github.io/helm-charts"
  chart            = "prometheus"
  namespace        = var.prometheus_namespace

  values = [
    yamlencode({
      server = {
        logLevel = "debug"
        extraFlags = [
          "web.enable-lifecycle",
          "web.enable-otlp-receiver",
        ]
        service = {
          additionalPorts = [{
            name       = "otlp"
            port       = 9090
            targetPort = 9090
          }]
        }
        ingress = {
          annotations = {
            "kubernetes.io/ingress.class" = "nginx"
          }
          enabled          = true
          hosts            = ["k8s-nginxing-nginxing-d7beae59c9-b40cb9be01af3e40.elb.us-west-2.amazonaws.com"]
          ingressClassName = "nginx"
          path             = "/"
        }
      }
    })
  ]
}
