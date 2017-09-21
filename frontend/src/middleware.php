<?php
// Application middleware

$app->add(new \Slim\Middleware\Session([
    'name' => 'session',
    'autorefresh' => true,
    'lifetime' => '1 hour'
]));