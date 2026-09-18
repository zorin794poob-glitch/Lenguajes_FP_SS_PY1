#!/bin/bash
echo "Instalando Graphviz..."
sudo apt update
sudo apt install -y graphviz
echo
echo "Comprobando instalación:"
dot -V
