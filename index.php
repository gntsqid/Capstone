<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Select an Option</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #333;
            color: #fff;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }
        .button-container {
            margin: 20px;
        }
        .button {
            margin: 10px 0;
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
            border: none;
            background-color: #4CAF50;
            color: white;
            border-radius: 5px;
            text-align: center;
            width: 200px;
        }
        .button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <h1>Select an Option</h1>
    <div class="button-container">
        <form action="og.php" method="get">
            <button type="submit" class="button">OG SITE</button>
        </form>
        <form action="map.php" method="get">
            <button type="submit" class="button">MAP</button>
        </form>
        <form action="https://github.com/gntsqid" method="get" target="_blank">
            <button type="submit" class="button">GITHUB</button>
        </form>
    </div>
    <!-- To add a third button, copy one of the <form> blocks above and change the action and button text accordingly -->
</body>
</html>

