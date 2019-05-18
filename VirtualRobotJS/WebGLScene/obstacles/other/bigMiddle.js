const config = {
    floor: {
        size: { x: 25, y: 25 }
    },
    player: {
        position: { x: 0.14, y: 0.2 },
        speed: 0.2
    },
    sonars: [
        {
            name: "sonar1",
            position: { x: 0.1, y: 0.05 },
            senseAxis: { x: false, y: true }
        },
        {
            name: "sonar2",
            position: { x: 0.95, y: 0.9},
            senseAxis: { x: true, y: false }
        } 
     ],
    movingObstacles: [
//        {
//            name: "moving-obstacle-1",
//            position: { x: .5, y: .4 },
//            directionAxis: { x: true, y: false },
//            speed: 1,
//            range: 4
//        },
//        {
//            name: "moving-obstacle-2",
//            position: { x: .5, y: .2 },
//            directionAxis: { x: true, y: true },
//            speed: 2,
//            range: 2
//        }
    ],
    staticObstacles: [
       {
           name: "middle",
           centerPosition: { x: 0.5, y: 0.5},
           size: { x: 0.4, y: 0.4}
       },
        {
        	name: "wallUp",
        	centerPosition: { x: 0.58, y: 0.98},
        	size: { x: 0.8, y: 0.01 }
        },
         {
            name: "wallDown",
            centerPosition: { x: 0.42, y: 0.02},
            size: { x: 0.8, y: 0.01}
        },
       {
            name: "wallLeft",
            centerPosition: { x: 0.02, y: 0.42},
            size: { x: 0.01, y: 0.8}
        },
        {
            name: "wallRight",
            centerPosition: { x: 0.98, y: 0.58},
            size: { x: 0.01, y: 0.8}
        }
    ]
}

export default config;
