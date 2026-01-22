#!/bin/bash

export TF_IN_AUTOMATION="1"

LIFERAY_CLOUD_DIR="$(dirname "$0")/.."

_log_message() {
  echo
  echo "$1"
}

_terraform_init_and_apply() {
  pushd "$1"
  terraform init
  terraform apply
  popd
}

_setup_aws_gitops_platform() {
  _terraform_init_and_apply "${LIFERAY_CLOUD_DIR}/terraform/aws/gitops/platform"
}

_setup_aws_gitops_resources() {
  _terraform_init_and_apply "${LIFERAY_CLOUD_DIR}/terraform/aws/gitops/resources"
}

port_forward_argocd() {
  local argocd_password
  argocd_password=$(kubectl get secret argocd-initial-admin-secret \
    --namespace argocd \
    --output jsonpath="{.data.password}" \
    | base64 --decode)

  _log_message "Port-forwarding the ArgoCD service at localhost:8080. Login with Username: admin and Password: ${argocd_password} to proceed with GitOps setup."

  kubectl port-forward service/argocd-server 8080:443 \
      --namespace argocd
}

setup_aws_gitops() {
  _log_message "Setting up GitOps Infrastructure..."

  _setup_aws_gitops_platform
  _setup_aws_gitops_resources

  _log_message "GitOps Infrastructure setup complete."
}

setup_aws_eks() {
  _log_message "Attempting to login to your AWS account via SSO..."

  aws sso login

  _log_message "Setting up the AWS EKS cluster..."

  _terraform_init_and_apply "${LIFERAY_CLOUD_DIR}/terraform/aws/eks"

  aws eks update-kubeconfig \
     --name $(terraform output -raw cluster_name) \
     --region $(terraform output -raw region)

  export KUBE_CONFIG_PATH="${HOME}/.kube/config"

  _log_message "AWS EKS cluster setup complete."
}

setup_aws_eks
setup_aws_gitops
port_forward_argocd
