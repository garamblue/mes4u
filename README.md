# mes4u

<p align="center">
  <a href="https://github.com/vuejs/core">
    <img src="https://img.shields.io/badge/vue-3.5-brightgreen.svg" alt="vue">
  </a>
  <a href="https://element-plus.org">
    <img src="https://img.shields.io/badge/element--plus-2.14-brightgreen.svg" alt="element-plus">
  </a>
  <a href="./LICENSE">
    <img src="https://img.shields.io/badge/license-LGPL_2.1-blue" alt="license">
  </a>
  <a href="https://github.com/sindohmes/MESTEST/releases">
    <img src="https://img.shields.io/badge/release-v1.0-blue" alt="GitHub release">
  </a>
</p>

Eng / [한글](./README.ko-KR.md)

## Introduce

mes4u is a web-based open source Manufacturing Execution System (MES) developed by ©Sindoh.

The system is developed based on functions of our previous MES used on our manufacturing sites and know-how gained from it. We share the program as a open source software for those who interests in MES. The current version (v1) provides core functions and master data needed on the manufacturing sites. We'll improve the program by adding more enhanced functions for manufacturing sites such as inventory and material management.



+ [Installation and How to Use](./Installation.md)
+ [Main Features](./features.md)
+ [Version History](./version.ko-KR.md)

## System Environment

The system's environment is below.

- Database: PostgreSQL 9.6.18
- Back-end
  + Framework: Spring Boot
  + Java: 17 (17.0.9)
  + Gradle: 7.6.4 (Gradle Wrapper)
- Front-end
  + Vue.js: 3.5 (Vite)
  + Template: [Vue-element-admin](https://github.com/PanJiaChen/vue-element-admin) (made by PanJiaChen)
  + UI: [Element Plus](https://element-plus.org)

## Prerequisites

To install and use MES, you need the followings:

- Necessary Programs
  + [node.js](https://nodejs.org/) (20.19+ or 22.12+)
  + [git](https://git-scm.com/)

The use of mes4u is not limited to manufacturing sites only. If you want to customize it, you need to modify the source codes. To run and test the program, we recommend using the following programs.
 
- Recommended servers and tools
  + Spring Tool Suite 4
  + Visual Studio Code
  + PostgreSQL
  + JDK 17
  
Recommended web browsers

- Chrome 
- Firefox
- Internet Explorer 10+
- Microsoft Edge 

## License

[GNU LGPL v2.1](./LICENSE)

Copyright (c) 2020-present Sindoh
