package com.project5.rcrsms.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; 

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @NotBlank(message = "Room name is required") 
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Capacity is required") 
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 1000, message = "Capacity cannot exceed 1000")
    @Column(nullable = false)
    private Integer capacity;

    @NotBlank(message = "Location is required") 
    @Column(nullable = false)
    private String location; 

    public Room() {}

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}