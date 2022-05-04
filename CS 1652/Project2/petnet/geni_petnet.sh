#! /bin/bash

sudo apt update
sudo apt install -y ifupdown

echo "auto petnet
iface petnet inet manual
# bridge_ports
  pre-up ip link add name petnet_bridge type bridge
  pre-up ip tuntap add dev petnet_tap1 mode tap user $USER
  pre-up ip link set petnet_tap1 master petnet_bridge
  up ip addr add 192.168.201.1/24 brd + dev petnet_bridge
  up ip link set dev petnet_bridge up
  up ip link set dev petnet_tap1 up
  post-up ip link set petnet_bridge address a6:94:0d:b7:92:e9
  down ip link set dev petnet_tap1 down
  down ip link set dev petnet_bridge down
  post-down ip tuntap del dev petnet_tap1 mode tap
  post-down ip link delete petnet_bridge type bridge" | sudo tee --append /etc/network/interfaces
