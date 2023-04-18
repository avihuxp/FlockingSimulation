
![Flocking Simulation Banner](README/FlockingSimulationBanner.png)

<p align = "center">  
    A simulation of flocking behaviours in nature</p>  
<div align = "center">  

![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/navendu-pottekkat/awesome-readme?include_prereleases)  ![GitHub last commit](https://img.shields.io/github/last-commit/avihuxp/FlockingSimulation?color=orange)  ![GitHub issues](https://img.shields.io/github/issues/avihuxp/FlockingSimulation?color=yellow)  ![GitHub pull requests](https://img.shields.io/github/issues-pr/avihuxp/FlockingSimulation?color=yellow)  ![GitHub repo size](https://img.shields.io/github/repo-size/avihuxp/FlockingSimulation)  ![GitHub](https://img.shields.io/github/license/avihuxp/FlockingSimulation)

<a href="https://github.com/avihuxp/FlockingSimulation/issues">Report Bug</a> <a href="https://github.com/avihuxp/FlockingSimulation/issues">Request Feature</a>
</div>  

## About the project

This project is a work-in-progress implementation of a flocking simulation of  
agents using Java and Processing. The goal of this project is to explore and  
experiment with different flocking behaviors and to create visually interesting  
and dynamic simulations.
The project currently includes collision detection with both Euclidean distance measurements and with a quadtree data structure that I implemented. As a work-in-progress project, there may be bugs and incomplete features, but I welcome feedback and contributions from others interested in exploring flocking  
simulations.

## Table of context

- [Demo](#demo)
- [Flocking Simulation Explained](#Flocking-behaviour-explained)
- [Requirements](#Requirements)
- [Installation](#installation)
- [Usage](#Usage)
- [Features](#features)
- [Resources](#Resources-and-Acknowledgments)

## Demo

<div align="center">  

</div>  
<video controls autoplay>
  <source src="https://user-images.githubusercontent.com/74983143/232906737-f554ee09-e947-4ae4-a486-cb249b726008.mp4" type="video/mp4">
</video>




For a full demo video check this [link](https://youtu.be/npPt1FX66dg).
<p align="right">(<a href="#about-the-project">back to top</a>)</p>  

## Flocking behaviour explained

Flocking behavior is a phenomenon observed in nature where groups of 
animals move in a coordinated way without a centralized control. Examples include 
flocks of birds, schools of fish, and swarms of locusts. Flocking behavior 
has been studied in various scientific fields, including biology, physics, 
and computer science, as it represents a complex system that emerges from 
the interactions between individual agents. By understanding the underlying 
principles of flocking behavior, we can apply this knowledge to various fields, such as robotics, animation, and game development.

The following implementation is based on the flocking behavior and boid simulation characteristics presented by [Craig Reynolds](http://www.red3d.com/cwr/index.html).

The three core principles of flocking behavior are alignment, separation, 
and cohesion, which were implemented in this project.

<figure align="center">
  <img src="http://www.red3d.com/cwr/boids/images/alignment.gif" alt="Trulli" align="center">
<p>
 Alignment
 </p>
</figure>  

* **Alignment** - *steer towards the average heading of local flockmates* - refers to the tendency of an individual agent to align its velocity with the average velocity of its neighboring agents. This principle allows for a coordinated movement of the flock towards a common direction, creating a sense of unity and common purpose.

<figure align="center">
  <img src="http://www.red3d.com/cwr/boids/images/separation.gif" alt="Trulli" align="center">
<p>
 Separation
 </p>
</figure>

* **Separation** - *"steer to avoid crowding local flockmates"* - refers to the tendency of an individual agent to maintain a  minimum distance from its neighboring agents. This principle helps to prevent collisions between agents and maintain a sense of personal space. By avoiding collisions, agents can move freely within the flock, which contributes to the overall fluidity of the flocking behavior.

<figure align="center">
  <img src="http://www.red3d.com/cwr/boids/images/cohesion.gif" alt="Trulli" align="center">
<p>
 Cohesion
 </p>
</figure>

* **Cohesion** - *steer to move toward the average position of local flockmates* - refers to the tendency of an individual agent to move towards the center of mass of its neighboring agents. This principle creates a sense of togetherness and group cohesion within the flock. By moving towards the center of mass, agents can maintain a consistent distance from their neighbors and avoid becoming isolated from the flock.

By implementing these principles, this project simulates the emergent behavior  
of a flock, which creates visually interesting and dynamic simulations. This  
project allows for experimentation with different parameters to explore the  
different behaviors that can arise from these principles. Additionally, the  
quadtree data structure implemented in this project allows for efficient  
collision detection, which improves performance for large numbers of agents.

## Requirements

The program requires the following to run:

- JAVA 8
- Processing Core

I highly recommend downloading and using the processing native  
IDE [here](https://processing.org/download)

## Installation

Clone the repo:
 ```bash  
 git clone https://github.com/avihuxp/FlockingSimulation.git  
 ```  

## Usage

There are many parameters one can toy with, the main ones are:

* `BoidSimulation.NUM_OF_VEHICLES` - Sets the number of elements in the   
  simulation.
* `BoidSimulation.NUM_OF_FLOCKS` - Sets the number of flocks in the  
  simulation. Each flock has its own color, and does not preform alignment   
  and cohesion with other flocks.
* `BoidSimulation.WITH_QUAD` - Set to true to use QuadTree for collision   
  detection.

<p align="right">(<a href="#about-the-project">back to top</a>)</p>  

## Features

* [ ] Implement spatial hashing to improve runtime performance
* [ ] Add UI for parameter  tweaking


See the [open issues](https://github.com/avihuxp/FlockingSimulation/issues)  
for a full list of proposed features (and known issues).

<p align="right">(<a href="#about-the-project">back to top</a>)</p>  

## Resources and Acknowledgments

This project is heavily inspired and implemented with the help of the following  
sources:

1. The aforementioned ["Boids" article](http://www.red3d.com/cwr/boids/) by [Craig Reynolds](http://www.red3d.com/cwr/index.html)
2. The Coding Train's video on [Flocking Simulation](https://www.youtube.com/watch?v=mhjuuHl6qHM&t=33s),  which is when I first became familiar with this subject.
<p align="right">(<a href="#about-the-project">back to top</a>)</p>  

## Copyright

MIT License

Copyright (c) 2023 avihuxp

Permission is hereby granted, free of charge, to any person obtaining a copy of  
this software and associated documentation files (the "Software"), to deal in  
the Software without restriction, including without limitation the rights to  
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies  
of the Software, and to permit persons to whom the Software is furnished to do  
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all  
copies or substantial portions of the Software.

<p align="right">(<a href="#about-the-project">back to top</a>)</p>
