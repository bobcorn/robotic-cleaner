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
	movingObstacles: [],
	staticObstacles: [
	{
		name: "middle1",
		centerPosition: { x: 0.25, y: 0.5},
		size: { x: 0.48, y: 0.01}
	},
	{
		name: "middle2",
		centerPosition: { x: 0.5, y: 0.25},
		size: { x: 0.01, y: 0.48}
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
